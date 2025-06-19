package at.technikum.energyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import at.technikum.energyapi.model.EnergyRecord;

public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {}
