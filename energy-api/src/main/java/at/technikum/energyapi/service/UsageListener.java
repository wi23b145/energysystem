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

    // Repository für EnergyUsage und EnergyRecord, um auf die Datenbank zuzugreifen
    private final EnergyUsageRepository usageRepo;
    private final EnergyRecordRepository recordRepo;

    // RabbitTemplate für das Senden von Nachrichten an RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    // Konstruktor mit Abhängigkeitsinjektion für die Repositorys und RabbitTemplate
    // Diese Methode stellt sicher, dass die benötigten Objekte zur Verfügung gestellt werden, wenn der Service instanziiert wird.
    public UsageListener(EnergyUsageRepository usageRepo,
                         EnergyRecordRepository recordRepo,
                         RabbitTemplate rabbitTemplate) {
        this.usageRepo = usageRepo;  // Speichert das Repository für EnergyUsage
        this.recordRepo = recordRepo;  // Speichert das Repository für EnergyRecord
        this.rabbitTemplate = rabbitTemplate;  // Speichert das RabbitTemplate für den Nachrichtenaustausch
    }

    // Diese Methode wird durch @RabbitListener ausgelöst, wenn eine Nachricht in der "energy-data-queue" empfangen wird.
    // Sie verarbeitet die empfangene Nachricht und speichert die relevanten Daten in der Datenbank.
    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "energy-data-queue", durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "energy.exchange", type = "direct"),
            key = "energy"
    ))
    @Transactional  // Alle Datenbankoperationen in dieser Methode werden in einer Transaktion ausgeführt
    public void handleMessage(@Payload String message) {
        try {
            // Simuliere eine Verzögerung von 10 Sekunden, um die Verarbeitung der Nachricht absichtlich zu verlangsamen (z. B. für Tests)
            Thread.sleep(10000);

            // Konvertiere die empfangene Nachricht in ein JSON-Objekt
            JSONObject json = new JSONObject(message);

            // Extrahiere relevante Felder aus der Nachricht:
            // - `type` (Nachrichtentyp, z.B. "PRODUCER", "USER", "ENERGY")
            // - `kwh` (Die verbrauchten Kilowattstunden)
            // - `datetime` (Der Zeitstempel der Nachricht)
            String type = json.getString("type");  // Nachrichtentyp (PRODUCER, USER, ENERGY)
            double kwh = json.getDouble("kwh");  // Die verbrauchten Kilowattstunden
            LocalDateTime timestamp = LocalDateTime.parse(json.getString("datetime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // Der Zeitstempel der Nachricht

            // Berechne die Stunde, um Aggregationen auf Stundenbasis durchzuführen (Setze Minuten, Sekunden und Nanosekunden auf 0)
            LocalDateTime hour = timestamp.withMinute(0).withSecond(0).withNano(0);

            // Versuche, das EnergyRecord für die berechnete Stunde aus der Datenbank zu holen. Wenn es nicht existiert, wird ein neues erstellt.
            EnergyRecord record = recordRepo.findByTimestamp(hour).orElseGet(() -> {
                EnergyRecord newRecord = new EnergyRecord();
                newRecord.setTimestamp(hour);
                newRecord.setProduction(0.0);  // Initialisierte Produktion auf 0
                newRecord.setConsumption(0.0);  // Initialisierter Verbrauch auf 0
                return newRecord;
            });

            // Verarbeite die Nachricht je nach Typ
            if (type.equalsIgnoreCase("PRODUCER")) {
                // Wenn es sich um einen PRODUCER handelt, wird nur die Produktion berücksichtigt
                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setCommunityName(json.getString("association"));  // Gemeinschaftsname
                usage.setProduction(kwh);  // PRODUCER stellt Strom bereit
                usage.setConsumption(0);  // PRODUCER hat keinen Verbrauch
                usage.setGridUsed(0);  // PRODUCER verwendet kein Stromnetz
                usageRepo.save(usage);  // Speichere den Energieverbrauch des PRODUCER
                record.setProduction(record.getProduction() + kwh);  // Aktualisiere die Produktion im EnergyRecord
            } else if (type.equalsIgnoreCase("USER")) {
                // Wenn es sich um einen USER handelt, wird nur der Verbrauch berücksichtigt
                record.setConsumption(record.getConsumption() + kwh);  // Füge den Verbrauch hinzu

                // Speichere auch den EnergyUsage für das Tracking des einzelnen Ereignisses
                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setCommunityName(json.optString("association", "UNKNOWN"));  // Wenn kein Gemeinschaftsname, dann "UNKNOWN"
                usageRepo.save(usage);
            } else if (type.equalsIgnoreCase("ENERGY")) {
                // Wenn es sich um eine allgemeine "ENERGY"-Nachricht handelt, speichere sowohl Produktion als auch Verbrauch
                double production = json.optDouble("production", 0);  // Falls keine Produktion vorhanden, setze auf 0
                double consumption = json.optDouble("consumption", 0);  // Falls kein Verbrauch vorhanden, setze auf 0
                String communityName = json.optString("community_name", "UNKNOWN");  // Gemeinschaftsname, falls nicht vorhanden "UNKNOWN"

                // Speichere die EnergyUsage
                EnergyUsage usage = new EnergyUsage();
                usage.setTimestamp(timestamp);
                usage.setKwh(kwh);
                usage.setProduction(production);
                usage.setConsumption(consumption);
                usage.setCommunityName(communityName);
                usageRepo.save(usage);

                // Aktualisiere das Aggregat im EnergyRecord mit den Produktions- und Verbrauchswerten
                record.setProduction(record.getProduction() + production);
                record.setConsumption(record.getConsumption() + consumption);
            } else {
                // Wenn der Nachrichtentyp unbekannt ist, gebe eine Warnung aus und breche ab
                System.out.println("Unbekannter Nachrichtentyp: " + type);
                return;
            }

            // Speichern des aktualisierten EnergyRecord in der Datenbank
            recordRepo.save(record);

            // Sende eine Nachricht an den "percent"-Service zur Aktualisierung der Aggregationsdaten (Produktion, Verbrauch)
            JSONObject update = new JSONObject();
            update.put("timestamp", hour.toString());  // Setze den Zeitstempel der Stunde
            update.put("production", record.getProduction());  // Setze die aktuelle Produktion
            update.put("consumption", record.getConsumption());  // Setze den aktuellen Verbrauch

            // Sende die Nachricht an den RabbitMQ-Exchange, damit sie vom "percent"-Service weiterverarbeitet wird
            rabbitTemplate.convertAndSend(EXCHANGE, "percent", update.toString());

            System.out.println("Verarbeitet und weitergeleitet: " + update);

        } catch (Exception e) {
            // Falls ein Fehler auftritt, gebe eine Fehlermeldung aus
            System.err.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }

    // RabbitListener für die Queue "percent.queue" - Verarbeitet Nachrichten zur Prozentberechnung
    @RabbitListener(queues = "percent.queue")
    @Transactional
    public void handlePercentMessage(String message) {
        // Gibt die empfangene Nachricht aus (aktuell keine Verarbeitung)
        System.out.println("Nachricht aus percent.queue empfangen: " + message);
    }

    // RabbitListener für die Queue "usage.queue" - Gibt empfangene Nachrichten aus (aktuell keine Verarbeitung)
    @RabbitListener(queues="usage.queue")
    public void receiveMessage(String message){
        System.out.println("Received message: " + message);
    }

    // RabbitListener für die Queue "producer.queue" - Gibt empfangene Nachrichten aus (aktuell keine Verarbeitung)
    @RabbitListener(queues = "producer.queue")
    public void handleProducerQueue(String message) {
        System.out.println("Nachricht aus producer.queue empfangen: " + message);
    }

    // PostConstruct: Diese Methode wird nach der Erstellung des Beans aufgerufen und sendet eine Test-Nachricht in den RabbitMQ-Exchange
    @PostConstruct
    public void sendTestUserMessage(){
        // Erstelle eine Testnachricht für einen User
        JSONObject json = new JSONObject();
        json.put("datetime", "2025-06-23T17:00:00");  // Zeitpunkt der Nachricht
        json.put("association", "COMMUNITY");  // Gemeinschaftsname
        json.put("type", "USER");  // Nachrichtentyp
        json.put("kwh", 0.25);  // Verbrauch in kWh

        // Sende die Testnachricht an den RabbitMQ-Exchange
        rabbitTemplate.convertAndSend("energy.exchange", "energy", json.toString());
    }
}

//