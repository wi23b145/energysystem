package at.technikum.energyapi.service;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static at.technikum.energyapi.config.RabbitMQConfig.EXCHANGE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ProducerService {

    // Das RabbitTemplate wird verwendet, um Nachrichten an RabbitMQ zu senden.
    private final RabbitTemplate rabbitTemplate;

    // Zufallsgenerator für die Erzeugung zufälliger Werte (z. B. für Produktions- und Verbrauchswerte)
    private final Random random = new Random();

    // Ein Scheduler, der Nachrichten periodisch mit zufälligen Verzögerungen sendet
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Konstruktor: Abhängigkeitsinjektion des RabbitTemplate
    // Hier wird das RabbitTemplate für spätere Nachrichtenversendung gespeichert
    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Diese Methode wird nach der Initialisierung des Beans durch @PostConstruct aufgerufen
    // Sie startet den Produktionszyklus, indem sie den ersten Produktionszyklus startet
    @PostConstruct
    public void startProducing() {
        // Der erste Produktionszyklus wird gestartet
        scheduleNextMessage();
    }

    // Diese Methode plant die nächste Nachrichtensendung mit einer zufälligen Verzögerung
    private void scheduleNextMessage() {
        // Eine zufällige Verzögerung zwischen 1 und 5 Sekunden
        int delaySeconds = 1 + random.nextInt(5);  // Zufallszahl zwischen 1 und 5
        // Der Scheduler wird verwendet, um die nächste Nachricht nach der zufälligen Verzögerung zu senden
        scheduler.schedule(this::sendMessage, delaySeconds, TimeUnit.SECONDS);
    }

    // Diese Methode erzeugt und sendet Nachrichten (PRODUCER, ENERGY, PERCENT, USAGE) an RabbitMQ
    private void sendMessage() {
        // Holen des aktuellen Zeitstempels
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // Zeitstempel im ISO-8601-Format

        // PRODUCER Nachricht (simuliert Energieproduktion)
        JSONObject messageProduced = new JSONObject();

        // Setze den Typ der Nachricht auf "PRODUCER", um anzuzeigen, dass diese Nachricht von einem PRODUCER kommt
        messageProduced.put("type", "PRODUCER");

        // Setze den Namen der Gemeinschaft, die die Nachricht sendet
        // "COMMUNITY" ist der Name der erzeugenden Gemeinschaft
        messageProduced.put("association", "COMMUNITY");

        // Erzeuge eine zufällige Menge an produzierter Energie
        // Der Wert wird zufällig zwischen 0.001 und 0.005 kWh gewählt.
        // Die Formel 0.001 + (0.004 * random.nextDouble()) gibt eine Zufallszahl im Bereich von 0.001 bis 0.005 zurück.
        messageProduced.put("production", 0.001 + (0.004 * random.nextDouble()));

        // Setze den Zeitstempel der Nachricht im ISO 8601-Format, um den Zeitpunkt der Produktion festzuhalten
        // Der Zeitstempel wird aus der aktuellen Zeit generiert
        messageProduced.put("datetime", timestamp);

        // Sende die Nachricht an RabbitMQ, der Routing-Schlüssel "produced" gibt an, dass es sich um eine PRODUCER-Nachricht handelt
        rabbitTemplate.convertAndSend(EXCHANGE, "produced", messageProduced.toString());
        // Ausgabe der gesendeten Nachricht zur Überprüfung
        System.out.println("Sent PRODUCER message: " + messageProduced);

        // ENERGY Nachricht (simuliert sowohl Produktion als auch Verbrauch)
        JSONObject messageEnergy = new JSONObject();
        messageEnergy.put("type", "ENERGY");
        messageEnergy.put("community_name", "COMMUNITY");

        // Erzeugt zufällige Produktions- und Verbrauchswerte (zwischen 0.001 und 0.005 kWh)
        messageEnergy.put("production", 0.001 + (0.004 * random.nextDouble()));
        messageEnergy.put("consumption", 0.001 + (0.004 * random.nextDouble()));

        // Der Zeitstempel der Nachricht wird gesetzt
        messageEnergy.put("datetime", timestamp);

        // Zufälliger kWh-Wert für die Nachricht
        messageEnergy.put("kwh", 0.001 + (0.004 * random.nextDouble()));

        // Sende die Nachricht an RabbitMQ
        rabbitTemplate.convertAndSend(EXCHANGE, "energy", messageEnergy.toString());
        System.out.println("Sent ENERGY message: " + messageEnergy);

        // PERCENT Nachricht (simuliert den Prozentsatz der Energieproduktion)
        JSONObject messagePercent = new JSONObject();
        messagePercent.put("type", "PERCENT");
        messagePercent.put("association", "COMMUNITY");

        // Zufälliger Prozentsatz zwischen 0 und 99
        messagePercent.put("percentage", random.nextInt(100));

        // Zufällige Produktionsmenge
        messagePercent.put("production", 0.001 + (0.004 * random.nextDouble()));

        // Kein Verbrauch in dieser Nachricht
        messagePercent.put("consumption", 0.0);

        // Die Nachricht wird an RabbitMQ gesendet (an den Routing-Schlüssel "percent")
        messagePercent.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        rabbitTemplate.convertAndSend(EXCHANGE, "percent", messagePercent.toString());
        System.out.println("Sent PERCENT message: " + messagePercent);

        // USAGE Nachricht (simuliert tatsächlichen Verbrauch)
        JSONObject messageUsage = new JSONObject();
        messageUsage.put("type", "USAGE");
        messageUsage.put("community_name", "COMMUNITY");

        // Zufälliger Verbrauchswert zwischen 0.001 und 0.005 kWh
        messageUsage.put("consumption", 0.001 + (0.004 * random.nextDouble()));

        // Zufälliger kWh-Wert
        messageUsage.put("kwh", 0.001 + (0.004 * random.nextDouble()));

        // Setze den Zeitstempel
        messageUsage.put("datetime", timestamp);

        // Sende die Nachricht an RabbitMQ
        rabbitTemplate.convertAndSend(EXCHANGE, "used", messageUsage.toString());
        System.out.println("Sent USAGE message: " + messageUsage);

        // Plant die nächste Nachricht nach einer zufälligen Verzögerung
        scheduleNextMessage();
    }

    // Diese Methode ermöglicht es, eine Test-Nachricht für den USER-Typ zu senden
    public void sendTestUserMessage() {
        // Erstelle eine Testnachricht für den USER-Typ
        JSONObject json = new JSONObject();
        json.put("timestamp", "2025-06-23T17:00:00");  // Festgelegter Zeitstempel
        json.put("association", "COMMUNITY");  // Gemeinschaftsname
        json.put("type", "USER");  // Nachrichtentyp
        json.put("kwh", 0.25);  // Zufälliger kWh-Wert für den Verbrauch

        // Sende die Test-Nachricht an RabbitMQ
        rabbitTemplate.convertAndSend(EXCHANGE, "energy", json.toString());
        System.out.println("Sent test USER message: " + json);
    }
}

