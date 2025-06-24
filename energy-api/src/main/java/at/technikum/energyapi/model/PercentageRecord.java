package at.technikum.energyapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity-Klasse für einen Prozentsatz-Datensatz.
 * Diese Klasse repräsentiert einen Eintrag in der Tabelle für Prozentwerte,
 * z.B. zur Speicherung von Grid-Abhängigkeit in Prozent.
 */
@Entity
public class PercentageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Eindeutige ID des Eintrags, automatisch generiert

    private double percentage; // Der gespeicherte Prozentwert (z.B. Netzabhängigkeit in %)

    // Standard-Konstruktor (wichtig für JPA)
    public PercentageRecord() {}

    /**
     * Konstruktor zur Initialisierung mit einem Prozentwert.
     * @param percentage Der Prozentwert, der gespeichert werden soll.
     */
    public PercentageRecord(double percentage) {
        this.percentage = percentage;
    }

    // Getter für die ID
    public Long getId() {
        return id;
    }

    // Getter für den Prozentwert
    public double getPercentage() {
        return percentage;
    }

    // Setter für den Prozentwert
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
