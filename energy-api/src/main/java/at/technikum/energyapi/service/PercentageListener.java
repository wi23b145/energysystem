package at.technikum.energyapi.service;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    // RabbitListener für die "percent.queue"
    // Diese Methode wird ausgelöst, wenn eine Nachricht in der Queue empfangen wird.
    @RabbitListener(queues = "percent.queue")
    public void handleMessage(String message) {
        try {
            // Konvertiere die empfangene Nachricht in ein JSON-Objekt
            JSONObject json = new JSONObject(message);

            // Extrahiere relevante Felder aus der Nachricht:
            // - production: Menge der erzeugten Energie
            // - consumption: Menge des verbrauchten Stroms
            // - timestamp: Zeitstempel der Nachricht
            double production = json.getDouble("production");  // Menge der erzeugten Energie
            double consumption = json.getDouble("consumption");  // Menge des verbrauchten Stroms
            String timestamp = json.getString("timestamp");  // Zeitstempel der Nachricht

            // Berechne den netzabhängigen Stromverbrauch (falls der Verbrauch größer als die Produktion ist)
            // gridUsed wird nur dann berechnet, wenn der Verbrauch größer ist als die Produktion
            double gridUsed = Math.max(consumption - production, 0);  // Wenn der Verbrauch größer als die Produktion ist, wird der Überschuss aus dem Netz bezogen

            // Berechne den Prozentsatz der Netzabhängigkeit (wenn kein Verbrauch, dann 0%)
            double percent = consumption > 0 ? (gridUsed / consumption) * 100 : 0;

            // Gebe die berechneten Werte in der Konsole aus
            // Die Ausgabe zeigt die Netzabhängigkeit in kWh sowie den Prozentsatz
            System.out.printf(
                    "Netzabhängigkeit für %s: %.2f kWh (%.2f%%)%n",
                    timestamp, gridUsed, percent
            );

        } catch (Exception e) {
            // Falls ein Fehler beim Verarbeiten der Nachricht auftritt, gebe eine Fehlermeldung aus
            System.err.println("Fehler beim Verarbeiten der Prozent-Nachricht: " + e.getMessage());
        }
    }
}
