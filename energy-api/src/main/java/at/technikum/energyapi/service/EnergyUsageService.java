package at.technikum.energyapi.service;

import at.technikum.energyapi.model.EnergyUsage;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


// Kennzeichnet die Klasse als Service-Komponente in Spring.
// Services enthalten die Geschäftslogik und sind Vermittler zwischen Controller und Repository.
@Service
public class EnergyUsageService {

    // Das Repository zum Zugriff auf die Datenbank-Tabelle für EnergyUsage.
    private final EnergyUsageRepository repository;

    // Konstruktor mit Dependency Injection des Repositories
    public EnergyUsageService(EnergyUsageRepository repository) {
        this.repository = repository;
    }

    /**
     * Liefert alle EnergyUsage-Datensätze aus der Datenbank.
     * Wird vom Controller z.B. für den /all-Endpunkt verwendet.
     * @return Liste aller EnergyUsage-Objekte
     */
    public List<EnergyUsage> findAll() {
        return repository.findAll();
    }

    /**
     * Liefert den neuesten (zeitlich letzten) EnergyUsage-Datensatz.
     * Nützlich für Live-Ansichten, z.B. /current-Endpunkt.
     * @return Das aktuellste EnergyUsage-Objekt
     */
    public EnergyUsage findLatest() {
        return repository.findFirstByOrderByTimestampDesc();
    }

    /**
     * Sucht alle EnergyUsage-Datensätze innerhalb eines bestimmten Zeitraums.
     * Wird z.B. für historische Datenabfragen genutzt.
     * @param start Startzeitpunkt (inklusive)
     * @param end Endzeitpunkt (inklusive)
     * @return Liste der EnergyUsage-Objekte im Zeitintervall
     */
    public List<EnergyUsage> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetween(start, end);
    }

    /**
     * Speichert einen neuen oder aktualisierten EnergyUsage-Datensatz in der Datenbank.
     * Wird z.B. von einem Consumer-Service beim Empfang neuer Nachrichten genutzt.
     * @param usage Das zu speichernde EnergyUsage-Objekt
     * @return Das gespeicherte EnergyUsage-Objekt (inkl. generierter ID)
     */
    public EnergyUsage save(EnergyUsage usage) {
        return repository.save(usage);
    }
}
//