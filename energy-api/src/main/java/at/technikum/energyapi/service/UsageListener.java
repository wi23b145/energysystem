package at.technikum.energyapi.service;

import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.model.EnergyUsage;
import at.technikum.energyapi.repository.EnergyRecordRepository;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static at.technikum.energyapi.config.RabbitMQConfig.EXCHANGE;

@Service
public class UsageListener {

    private final EnergyUsageRepository usageRepo;
    private final EnergyRecordRepository recordRepo;
    private final RabbitTemplate rabbitTemplate;

    public UsageListener(EnergyUsageRepository usageRepo,
                         EnergyRecordRepository recordRepo,
                         RabbitTemplate rabbitTemplate) {
        this.usageRepo = usageRepo;
        this.recordRepo = recordRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "energy-data-queue", durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "energy.exchange", type = "direct"),
            key = "energy"
    ))
    @Transactional
    public void handleMessage(@Payload String message) {
        try {
            Thread.sleep(10000);
            JSONObject json = new JSONObject(message);

            String type = json.getString("type");
            double kwh = json.getDouble("kwh");
            LocalDateTime timestamp = LocalDateTime.parse(json.getString("datetime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Stunde für Aggregation extrahieren
            LocalDateTime hour = timestamp.withMinute(0).withSecond(0).withNano(0);

            // Hole oder erstelle EnergyRecord für diese Stunde
            EnergyRecord record = recordRepo.findByTimestamp(hour).orElseGet(() -> {
                EnergyRecord newRecord = new EnergyRecord();
                newRecord.setTimestamp(hour);
                newRecord.setProduction(0.0);
                newRecord.setConsumption(0.0);
                return newRecord;
            });

            if (type.equalsIgnoreCase("PRODUCER")) {
                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setCommunityName(json.getString("association"));
                usage.setProduction(kwh);
                usage.setConsumption(0);
                usage.setGridUsed(0);  // PRODUCER nutzt kein Netz
                usageRepo.save(usage);
                record.setProduction(record.getProduction() + kwh);
            } else if (type.equalsIgnoreCase("USER")) {
                record.setConsumption(record.getConsumption() + kwh);

                // Zusätzlich EnergyUsage speichern (für Einzelereignis-Tracking)
                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setCommunityName(json.optString("association", "UNKNOWN"));
                usageRepo.save(usage);
            } else if (type.equalsIgnoreCase("ENERGY")){
                double production = json.optDouble("production", 0);
                double consumption = json.optDouble("consumption", 0);
                String communityName = json.optString("community_name", "UNKNOWN");

                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setProduction(production);
                usage.setConsumption(consumption);
                usage.setCommunityName(communityName);
                usageRepo.save(usage);

                // Update Aggregation im EnergyRecord
                record.setProduction(record.getProduction() + production);
                record.setConsumption(record.getConsumption() + consumption);

            }else {
                System.out.println("Unbekannter Nachrichtentyp: " + type);
                return;
            }

            recordRepo.save(record);

            // Sende Update-Nachricht weiter an Prozent-Service
            JSONObject update = new JSONObject();
            update.put("timestamp", hour.toString());
            update.put("production", record.getProduction());
            update.put("consumption", record.getConsumption());

            rabbitTemplate.convertAndSend(EXCHANGE, "percent", update.toString());

            System.out.println("Verarbeitet und weitergeleitet: " + update);

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "percent.queue")
    @Transactional
    public void handlePercentMessage(String message) {
        System.out.println("Nachricht aus percent.queue empfangen: " + message);

    }

    @RabbitListener(queues="usage.queue")
    public void receiveMessage(String message){
        System.out.println("Recieved message: " + message);
    }

    @RabbitListener(queues = "producer.queue")
    public void handleProducerQueue(String message) {
        System.out.println("Nachricht aus producer.queue empfangen: " + message);

    }

    @PostConstruct
    public void sendTestUserMessage(){
        JSONObject json = new JSONObject();
        json.put("datetime", "2025-06-23T17:00:00");
        json.put("association", "COMMUNITY");
        json.put("type", "USER");
        json.put("kwh", 0.25);

        rabbitTemplate.convertAndSend("energy.exchange", "energy", json.toString());

    }
}
