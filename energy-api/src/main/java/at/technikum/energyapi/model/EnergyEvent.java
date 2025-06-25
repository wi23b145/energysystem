package at.technikum.energyapi.model;

import java.io.Serializable;

public class EnergyEvent implements Serializable {
    private String source;
    private int amount;
    public EnergyEvent() {}
    public EnergyEvent(String source, int amount) {
        this.source = source;
        this.amount = amount;
    }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
