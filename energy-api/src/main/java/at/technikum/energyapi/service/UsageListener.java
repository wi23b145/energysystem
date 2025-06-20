package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.EnergyEvent;
import at.technikum.energyapi.model.EnergyUsage;
import at.technikum.energyapi.model.UsageRecord;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class UsageListener {
    private final EnergyUsageRepository usageRepo;
    private final AmqpTemplate           rabbit;

    public UsageListener(EnergyUsageRepository usageRepo, AmqpTemplate rabbit) {
        this.usageRepo = usageRepo;
        this.rabbit   = rabbit;
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCER_QUEUE)
    public void handleProduced(EnergyEvent evt) {
        EnergyUsage produced = new EnergyUsage("produced", evt.getAmount());
        usageRepo.save(produced);
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "used", produced);
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUsed(EnergyEvent evt) {
        EnergyUsage used = new EnergyUsage("used", evt.getAmount());
        usageRepo.save(used);
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "percent", used);
    }
}
