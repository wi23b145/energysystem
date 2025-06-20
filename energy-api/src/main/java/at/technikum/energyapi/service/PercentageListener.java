package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.UsageRecord;
import at.technikum.energyapi.model.PercentageRecord;
import at.technikum.energyapi.repository.PercentageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    private final PercentageRepository percentRepo;

    public PercentageListener(PercentageRepository percentRepo) {
        this.percentRepo = percentRepo;
    }

    @RabbitListener(queues = RabbitMQConfig.PERCENT_QUEUE)
    public void calculatePercentage(UsageRecord rec) {
        // Beispiel: Gesamtproduktion und -verbrauch aus DB holen, hier vereinfacht:
        double produced = percentRepo.sumByType("produced");
        double used     = percentRepo.sumByType("used");
        double percent  = (used / produced) * 100.0;
        PercentageRecord pr = new PercentageRecord(percent);
        percentRepo.save(pr);
    }
}
