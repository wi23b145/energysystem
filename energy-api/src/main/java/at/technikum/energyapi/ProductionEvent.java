package at.technikum.energyapi;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ProductionEvent {
    @Id
    @GeneratedValue
    Long id;

    String source;
    int amount;
}
