package at.technikum.energyapi.repository;

import at.technikum.energyapi.model.EnergyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {

    /**
     * Berechnet die Gesamtsumme aller 'amount'-Werte in der Tabelle EnergyRecord.
     * COALESCE sorgt dafür, dass 0 zurückgegeben wird, wenn noch keine Daten vorhanden sind.
     * Praktisch, um z.B. die Gesamtproduktion an Energie zu ermitteln.
     *
     * @return Summe der amount-Werte oder 0 wenn keine vorhanden
     */
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM EnergyRecord r")
    double sumProduced();

    /**
     * Sucht einen EnergyRecord-Eintrag, der genau den angegebenen Zeitstempel hat.
     * Nützlich, um zu prüfen, ob für eine bestimmte Stunde schon Daten vorliegen.
     *
     * @param timestamp Zeitpunkt der Stunde, z.B. '2025-06-23T19:00'
     * @return Optional mit EnergyRecord falls gefunden, sonst leer
     */
    Optional<EnergyRecord> findByTimestamp(LocalDateTime timestamp);

    /**
     * Holt den neuesten EnergyRecord-Eintrag basierend auf dem Zeitstempel.
     * Wird z.B. verwendet, um aktuelle Werte für die Anzeige abzurufen.
     *
     * @return Optional mit dem neuesten EnergyRecord, oder leer wenn keine Daten da sind
     */
    Optional<EnergyRecord> findTopByOrderByTimestampDesc();
}
