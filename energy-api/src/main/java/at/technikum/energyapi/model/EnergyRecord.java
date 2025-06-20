package at.technikum.energyapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Speichert ein Produktions-Event (Quelle + Menge) in der Datenbank.
 */
@Entity
public class EnergyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Quelle des Events (z.B. "producer" oder beliebiger Name) */
    private String source;

    /** Menge in kWh */
    private int amount;

    // Standard-Konstruktor für JPA
    public EnergyRecord() {}

    // Konstruktor zum einfachen Anlegen
    public EnergyRecord(String source, int amount) {
        this.source = source;
        this.amount = amount;
    }

    // Getter/Setter

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
