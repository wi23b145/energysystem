package at.technikum.energyapi.service;

import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.repository.EnergyRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyService {

    private final EnergyRecordRepository repository;

    public EnergyService(EnergyRecordRepository repository) {
        this.repository = repository;
    }

    public EnergyRecord save(EnergyRecord record) {
        return repository.save(record);
    }

    public EnergyRecord getLatestRecord() {
        return repository.findTopByOrderByTimestamp();
    }

    public List<EnergyRecord> getAllRecords() {
        return repository.findAll();
    }
}
