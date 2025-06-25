package at.technikum.energyapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.repository.EnergyRecordRepository;

// Kennzeichnet die Klasse als Spring-Service-Komponente
@Service
public class EnergyService {

    // Repository wird per Autowired automatisch injiziert,
    // um auf die Datenbank-Tabelle EnergyRecord zuzugreifen
    @Autowired
    private EnergyRecordRepository repository;

    /**
     * Speichert einen EnergyRecord-Datensatz in der Datenbank.
     * Wird z.B. von einem Consumer-Service oder Controller genutzt,
     * um neue oder aktualisierte Daten abzulegen.
     *
     * @param record Das zu speichernde EnergyRecord-Objekt
     * @return Das gespeicherte EnergyRecord-Objekt (inkl. generierter ID)
     */
    public EnergyRecord save(EnergyRecord record){
        return repository.save(record);
    }
}
