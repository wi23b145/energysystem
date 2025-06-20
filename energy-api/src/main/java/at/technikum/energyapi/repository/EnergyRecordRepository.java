package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.EnergyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM EnergyRecord r")
    double sumProduced();
}
