package at.technikum.energyapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity-Klasse für gespeicherte Energie-Nutzungsdaten.
 * Repräsentiert einen Datensatz in der Tabelle "energy_usage".
 */
@Entity
@Table(name = "energy_usage")
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Eindeutige ID des Eintrags, automatisch generiert

    private double production; // Produktionsmenge in kWh (z.B. erzeugte Energie der Community)
    private double consumption; // Verbrauchsmenge in kWh (z.B. Energieverbrauch der Community)
    private Double kwh; // Optional: Gesamt-kWh-Wert (kann z.B. für einzelne Nutzungsereignisse stehen)

    @Column(name = "grid_used")
    private double gridUsed; // Energieanteil aus dem öffentlichen Netz (Grid)

    @Column(name = "community_name")
    private String communityName; // Name der Community oder Quelle, z.B. "COMMUNITY"

    private LocalDateTime timestamp; // Zeitpunkt der Messung oder des Events

    // Standard-Konstruktor (wichtig für JPA)
    public EnergyUsage() {}

    /**
     * Konstruktor mit allen wichtigen Feldern für einfache Initialisierung.
     *
     * @param communityName Name der Community
     * @param production produzierte Energie in kWh
     * @param consumption verbrauchte Energie in kWh
     * @param gridUsed Energieanteil aus dem Grid in kWh
     * @param timestamp Zeitpunkt der Messung
     */
    public EnergyUsage(String communityName, double production, double consumption, double gridUsed, LocalDateTime timestamp) {
        this.communityName = communityName;
        this.production = production;
        this.consumption = consumption;
        this.gridUsed = gridUsed;
        this.timestamp = timestamp;
    }

    // Getter und Setter für alle Felder

    public Long getId() {
        return id;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String name) {
        this.communityName = name;
    }

    public double getProduction() {
        return production;
    }

    public void setProduction(double prod) {
        this.production = prod;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double cons) {
        this.consumption = cons;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double used) {
        this.gridUsed = used;
    }

    public Double getKwh() {
        return kwh;
    }

    public void setKwh(Double kwh) {
        this.kwh = kwh;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime ts) {
        this.timestamp = ts;
    }
}
