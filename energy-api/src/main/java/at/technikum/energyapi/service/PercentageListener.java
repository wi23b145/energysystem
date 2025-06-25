package at.technikum.energyapi.service;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    @RabbitListener(queues = "percent.queue")
    public void handleMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);

            double production = json.getDouble("production");
            double consumption = json.getDouble("consumption");
            String timestamp = json.getString("timestamp");

            double gridUsed = Math.max(consumption - production, 0);
            double percent = consumption > 0 ? (gridUsed / consumption) * 100 : 0;

            System.out.printf(
                    "Netzabhängigkeit für %s: %.2f kWh (%.2f%%)%n",
                    timestamp, gridUsed, percent
            );

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten der Prozent-Nachricht: " + e.getMessage());
        }
    }
}
