package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {

    @Query("SELECT COALESCE(SUM(e.consumption),0) FROM EnergyUsage e")
    double sumUsed();

    @Query("SELECT COALESCE(SUM(e.production),0) FROM EnergyUsage e")
    double sumProduced();
}
