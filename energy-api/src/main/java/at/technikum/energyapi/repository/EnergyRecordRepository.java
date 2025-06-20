package at.technikum.energyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import at.technikum.energyapi.model.EnergyRecord;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {

    EnergyRecord findTopByOrderByTimestamp();
}
