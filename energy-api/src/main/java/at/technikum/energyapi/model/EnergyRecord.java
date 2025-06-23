package at.technikum.energyapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Entity-Klasse für die Speicherung eines Energie-Produktions- oder Verbrauchs-Events.
 * Repräsentiert einen Datensatz in der Tabelle 'energy_record'.
 */
@Entity
public class EnergyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Eindeutige ID für jeden Datensatz, automatisch generiert

    /**
     * Quelle des Events, z.B. "producer" oder ein beliebiger Name,
     * der angibt, woher die Energie kommt oder wem der Eintrag zugeordnet wird.
     */
    private String source;

    /**
     * Menge in kWh (Kilowattstunden), als ganze Zahl gespeichert.
     * Repräsentiert typischerweise produzierte oder verbrauchte Energiemenge.
     */
    private int amount;

    /**
     * Zeitpunkt des Events – wichtig für zeitliche Zuordnung und Aggregation.
     */
    private LocalDateTime timestamp;

    /**
     * Produktionsmenge in kWh als Dezimalwert (für genauere Messungen).
     */
    private double production;

    /**
     * Verbrauchsmenge in kWh als Dezimalwert.
     */
    private double consumption;

    // Standard-Konstruktor für JPA (wird benötigt für Entitäten)
    public EnergyRecord() {}

    // Einfacher Konstruktor zur schnellen Initialisierung
    public EnergyRecord(String source, int amount) {
        this.source = source;
        this.amount = amount;
    }

    // Getter und Setter für alle Felder

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
}
