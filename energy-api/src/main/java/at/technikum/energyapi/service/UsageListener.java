package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.EnergyEvent;
import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.model.EnergyUsage;
import at.technikum.energyapi.repository.EnergyRecordRepository;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class UsageListener {

    private final EnergyRecordRepository recordRepo;
    private final EnergyUsageRepository usageRepo;
    private final AmqpTemplate rabbit;

    public UsageListener(EnergyRecordRepository recordRepo,
                         EnergyUsageRepository usageRepo,
                         AmqpTemplate rabbit) {
        this.recordRepo = recordRepo;
        this.usageRepo  = usageRepo;
        this.rabbit     = rabbit;
    }

    // Wenn eine Produktions-Nachricht ankommt, speichern wir eine EnergyRecord
    @RabbitListener(queues = RabbitMQConfig.PRODUCER_QUEUE)
    public void onProduced(EnergyEvent evt) {
        recordRepo.save(new EnergyRecord(evt.getSource(), evt.getAmount()));
    }
    @RabbitListener(queues = RabbitMQConfig.USAGE_QUEUE)
    public void onConsumed(EnergyEvent evt) {
        // Produktion = 0, Verbrauch = evt.getAmount(), Timestamp = jetzt
        EnergyUsage usage = new EnergyUsage(
                evt.getSource(),
                0.0,                        // keine Produktion
                evt.getAmount(),            // Verbrauch
                LocalDateTime.now()         // Zeitstempel
        );
        usageRepo.save(usage);

        // Update-Nachricht für Percentage-Service
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "percent", evt);
    }

}
