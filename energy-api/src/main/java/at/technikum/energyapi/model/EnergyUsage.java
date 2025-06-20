package at.technikum.energyapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EnergyUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String user;
    private int usage;

    public EnergyUsage() {}
    public EnergyUsage(String user, int usage) {
        this.user = user;
        this.usage = usage;
    }
    public Long getId() { return id; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public int getUsage() { return usage; }
    public void setUsage(int usage) { this.usage = usage; }
}
