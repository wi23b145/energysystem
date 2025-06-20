package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.PercentageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PercentageRepository extends JpaRepository<PercentageRecord, Long> {
    @Query("SELECT SUM(p.percentage) FROM PercentageRecord p")
    double sumByType(@Param("type") String type);
}
