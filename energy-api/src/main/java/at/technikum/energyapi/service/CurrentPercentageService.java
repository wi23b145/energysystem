package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.EnergyUsage;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class CurrentPercentageService {
    @Autowired
    private EnergyUsageRepository usageRepository;

    @RabbitListener(queues = RabbitMQConfig.PERCENT_QUEUE)
    public void handlePercentMessage(@Payload String message) {
        try {
            JSONObject json = new JSONObject(message);
            LocalDateTime timestamp = LocalDateTime.parse(json.getString("timestamp"));
            double production = json.getDouble("production");
            double consumption = json.getDouble("consumption");

            double gridUsed = Math.max(consumption - production, 0);

            EnergyUsage usage = new EnergyUsage();
            usage.setTimestamp(timestamp);
            usage.setProduction(production);
            usage.setConsumption(consumption);
            usage.setGridUsed(gridUsed);

            usageRepository.save(usage);

            System.out.println("Gespeichert: " + usage);

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Percent-Nachricht: " + e.getMessage());
        }
    }
}
