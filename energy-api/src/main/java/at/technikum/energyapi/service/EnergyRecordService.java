package at.technikum.energyapi.service;

import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.repository.EnergyRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class EnergyRecordService {

    private final EnergyRecordRepository recordRepo;

    public EnergyRecordService(EnergyRecordRepository recordRepo) {
        this.recordRepo = recordRepo;
    }

    public double calculateCurrentGridDependencyPercent() {
        EnergyRecord latestRecord = recordRepo.findTopByOrderByTimestampDesc()
                .orElseThrow(() -> new RuntimeException("Keine Energiedaten vorhanden"));

        double production = latestRecord.getProduction();
        double consumption = latestRecord.getConsumption();


        if (consumption == 0) return 0;

        double gridUsed = Math.max(consumption - production, 0);
        double percent = (gridUsed / consumption) * 100;
        percent = Math.min(Math.max(percent, 0), 100); // clamp between 0 and 100
        return percent;
    }

}
