package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.EnergyEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ProducerService {

    private final AmqpTemplate rabbit;
    private final Random rand = new Random();

    public ProducerService(AmqpTemplate rabbit) {
        this.rabbit = rabbit;
    }

    // alle 1–5 Sekunden eine Nachricht
    @Scheduled(fixedDelayString = "#{1000 + new java.util.Random().nextInt(4000)}")
    public void sendProduction() {
        int kwh = 1 + rand.nextInt(20); // sinnvolle kWh-Zahl
        EnergyEvent evt = new EnergyEvent("producer", kwh);
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "produced", evt);
        System.out.println("Produced: " + evt);
    }
}
