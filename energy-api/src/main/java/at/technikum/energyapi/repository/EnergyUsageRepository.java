package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {

    /**
     * Berechnet die Gesamtsumme aller verbrauchten Energie (consumption) in der Tabelle EnergyUsage.
     * COALESCE stellt sicher, dass bei fehlenden Daten 0 zurückgegeben wird.
     *
     * @return Summe der consumption-Werte oder 0, wenn keine Einträge vorhanden sind.
     */
    @Query("SELECT COALESCE(SUM(e.consumption),0) FROM EnergyUsage e")
    double sumUsed();

    /**
     * Berechnet die Gesamtsumme aller produzierten Energie (production) in der Tabelle EnergyUsage.
     * COALESCE stellt sicher, dass bei fehlenden Daten 0 zurückgegeben wird.
     *
     * @return Summe der production-Werte oder 0, wenn keine Einträge vorhanden sind.
     */
    @Query("SELECT COALESCE(SUM(e.production),0) FROM EnergyUsage e")
    double sumProduced();

    /**
     * Findet den aktuellsten Datensatz (nach Timestamp sortiert absteigend) aus der Tabelle EnergyUsage.
     * Wird verwendet, um die neuesten Messwerte abzurufen.
     *
     * @return Der neueste EnergyUsage-Eintrag.
     */
    EnergyUsage findFirstByOrderByTimestampDesc();

    /**
     * Liefert eine Liste von EnergyUsage-Datensätzen innerhalb eines bestimmten Zeitraums.
     *
     * @param start Anfang des Zeitbereichs (inklusive)
     * @param end Ende des Zeitbereichs (inklusive)
     * @return Liste aller EnergyUsage-Einträge im angegebenen Zeitraum.
     */
    List<EnergyUsage> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
