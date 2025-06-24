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

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void startProducing() {
        scheduleNextMessage();
    }

    private void scheduleNextMessage() {
        int delaySeconds = 1 + random.nextInt(5); // zufällige Verzögerung 1–5 Sekunden
        scheduler.schedule(this::sendMessage, delaySeconds, TimeUnit.SECONDS);
    }

    private void sendMessage() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // PRODUCER Nachricht (z.B. production)
        JSONObject messageProduced = new JSONObject();
        messageProduced.put("type", "PRODUCER");
        messageProduced.put("association", "COMMUNITY");
        messageProduced.put("production", 0.001 + (0.004 * random.nextDouble()));
        messageProduced.put("datetime", timestamp);
        rabbitTemplate.convertAndSend(EXCHANGE, "produced", messageProduced.toString());
        System.out.println("Sent PRODUCER message: " + messageProduced);

        // ENERGY Nachricht (production, consumption, community_name)
        JSONObject messageEnergy = new JSONObject();
        messageEnergy.put("type", "ENERGY");
        messageEnergy.put("community_name", "COMMUNITY");
        messageEnergy.put("production", 0.001 + (0.004 * random.nextDouble()));
        messageEnergy.put("consumption", 0.001 + (0.004 * random.nextDouble()));
        messageEnergy.put("datetime", timestamp);
        messageEnergy.put("kwh", 0.001 + (0.004 * random.nextDouble()));  // falls Listener kwh erwartet
        rabbitTemplate.convertAndSend(EXCHANGE, "energy", messageEnergy.toString());
        System.out.println("Sent ENERGY message: " + messageEnergy);

        JSONObject messagePercent = new JSONObject();
        messagePercent.put("type", "PERCENT");
        messagePercent.put("association", "COMMUNITY");
        messagePercent.put("percentage", random.nextInt(100));
        messagePercent.put("production", 0.001 + (0.004 * random.nextDouble()));
        messagePercent.put("consumption", 0.0);

// Wichtig: Feld so nennen, wie im Listener erwartet
        messagePercent.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));


        // USAGE Nachricht (consumption, kwh falls Listener erwartet)
        JSONObject messageUsage = new JSONObject();
        messageUsage.put("type", "USAGE");
        messageUsage.put("community_name", "COMMUNITY");
        messageUsage.put("consumption", 0.001 + (0.004 * random.nextDouble()));
        messageUsage.put("kwh", 0.001 + (0.004 * random.nextDouble()));  // falls erwartet
        messageUsage.put("datetime", timestamp);
        rabbitTemplate.convertAndSend(EXCHANGE, "used", messageUsage.toString());
        System.out.println("Sent USAGE message: " + messageUsage);

        scheduleNextMessage();
    }



    // Methode, um testweise eine USER-Nachricht zu senden
    public void sendTestUserMessage() {
        JSONObject json = new JSONObject();
        json.put("timestamp", "2025-06-23T17:00:00");
        json.put("association", "COMMUNITY");
        json.put("type", "USER");
        json.put("kwh", 0.25);

        rabbitTemplate.convertAndSend(EXCHANGE, "energy", json.toString());
        System.out.println("Sent test USER message: " + json);
    }
}
