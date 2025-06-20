package at.technikum.energyapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_usage")
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String communityName;
    private double production;
    private double consumption;

    private LocalDateTime timestamp;

    public EnergyUsage() {}

    public EnergyUsage(String communityName,
                       double production,
                       double consumption,
                       LocalDateTime timestamp) {
        this.communityName = communityName;
        this.production    = production;
        this.consumption   = consumption;
        this.timestamp     = timestamp;
    }

    // --- Getter & Setter ---

    public Long getId() { return id; }

    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public double getProduction() { return production; }
    public void setProduction(double production) {
        this.production = production;
    }

    public double getConsumption() { return consumption; }
    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
