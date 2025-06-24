package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.PercentageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PercentageRepository extends JpaRepository<PercentageRecord, Long> {


}