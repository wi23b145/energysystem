package at.technikum.energyapi.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.repository.EnergyRecordRepository;


@Service
public class EnergyService {

    @Autowired
    private EnergyRecordRepository repository;

    public EnergyRecord save(EnergyRecord record){
        return repository.save(record);
    }
}
