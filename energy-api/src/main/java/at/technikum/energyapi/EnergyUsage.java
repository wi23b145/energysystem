package at.technikum.energyapi;

import jakarta.persistence.*;

@Entity
@Table(name = "energy_usage")
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String communityName;
    private double production;
    private double consumption;
    private String timestamp;

    public EnergyUsage() {}

    public EnergyUsage(String communityName, double production, double consumption, String timestamp) {
        this.communityName = communityName;
        this.production = production;
        this.consumption = consumption;
        this.timestamp = timestamp;
    }
    // Getter und Setter
    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public double getProduction() {
        return production;
    }

    public void setProduction(double production) {
        this.production = production;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: für Debugging / Logging
    @Override
    public String toString() {
        return "EnergyUsage{" +
                "communityName='" + communityName + '\'' +
                ", production=" + production +
                ", consumption=" + consumption +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
