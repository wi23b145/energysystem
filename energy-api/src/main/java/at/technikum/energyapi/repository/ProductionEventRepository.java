package at.technikum.energyapi.repository;

import at.technikum.energyapi.ProductionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionEventRepository extends JpaRepository<ProductionEvent, Long> {
}
