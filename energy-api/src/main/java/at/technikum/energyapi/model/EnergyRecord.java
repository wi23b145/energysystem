package at.technikum.energyapi.model;


import jakarta.persistence.*;


@Entity
@Table(name="energy_record")
public class EnergyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String communityName;
    private double productionKw;
    private double consumptionKw;
    private String timestamp;

    public EnergyRecord() {
    }

    public EnergyRecord(String communityName, double productionKw, double consumptionKw, String timestamp) {
        this.communityName = communityName;
        this.productionKw = productionKw;
        this.consumptionKw = consumptionKw;
        this.timestamp = timestamp;
    }

    // Getter und Setter

    public long getId() {
        return id;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public double getProductionKw() {
        return productionKw;
    }

    public void setProductionKw(double productionKw) {
        this.productionKw = productionKw;
    }

    public double getConsumptionKw() {
        return consumptionKw;
    }

    public void setConsumptionKw(double consumptionKw) {
        this.consumptionKw = consumptionKw;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
