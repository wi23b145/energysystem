package at.technikum.energyapi;

public class EnergyUsage {
    private String communityName;
    private double production;     // in kWh
    private double consumption;    // in kWh
    private String timestamp;      // ISO 8601 Zeitformat

    // Konstruktor
    public EnergyUsage(String communityName, double production, double consumption, String timestamp) {
        this.communityName = communityName;
        this.production = production;
        this.consumption = consumption;
        this.timestamp = timestamp;
    }

    // Standard-Konstruktor (wird z.B. für JSON-Deserialisierung benötigt)
    public EnergyUsage() {
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
