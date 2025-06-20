package at.technikum.energyapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UsageRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private int amount;

    public UsageRecord() {}
    public UsageRecord(String eventType, int amount) {
        this.eventType = eventType;
        this.amount = amount;
    }
    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
