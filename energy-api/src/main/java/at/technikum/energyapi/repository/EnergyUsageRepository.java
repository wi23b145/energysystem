package at.technikum.energyapi.repository;

import at.technikum.energyapi.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {

}
