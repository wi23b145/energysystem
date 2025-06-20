package at.technikum.energyapi.service;

import at.technikum.energyapi.config.RabbitMQConfig;
import at.technikum.energyapi.model.EnergyEvent;
import at.technikum.energyapi.model.PercentageRecord;
import at.technikum.energyapi.repository.EnergyRecordRepository;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import at.technikum.energyapi.repository.PercentageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    private final EnergyRecordRepository recordRepo;
    private final EnergyUsageRepository usageRepo;
    private final PercentageRepository percentRepo;

    public PercentageListener(EnergyRecordRepository recordRepo,
                              EnergyUsageRepository usageRepo,
                              PercentageRepository percentRepo) {
        this.recordRepo = recordRepo;
        this.usageRepo  = usageRepo;
        this.percentRepo = percentRepo;
    }

    @RabbitListener(queues = RabbitMQConfig.PERCENT_QUEUE)
    public void onPercent(EnergyEvent evt) {
        double producedTotal = recordRepo.sumProduced();
        double usedTotal     = usageRepo.sumUsed();
        double pct           = producedTotal == 0 ? 0 : (usedTotal / producedTotal) * 100;
        percentRepo.save(new PercentageRecord(pct));
    }
}
