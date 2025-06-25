package at.technikum.energyapi.controller;

import at.technikum.energyapi.model.EnergyUsage;
import at.technikum.energyapi.service.EnergyRecordService;
import at.technikum.energyapi.service.EnergyUsageService;
import at.technikum.energyapi.service.ProducerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// Markiert die Klasse als REST-Controller, der HTTP-Anfragen verarbeitet
@RestController
// Basispfad für alle Endpunkte in diesem Controller ist "/energy"
@RequestMapping("/energy")
public class EnergyUsageController {

    // Service zur Verarbeitung von EnergyUsage-Daten (Geschäftslogik)
    private final EnergyUsageService energyUsageService;
    // Service, der Nachrichten über RabbitMQ verschickt (Producer)
    private final ProducerService producerService;
    // Service zur Verarbeitung und Berechnung auf Basis von EnergyRecord-Daten
    private final EnergyRecordService energyRecordService;

    /**
     * Konstruktor mit Abhängigkeiten-Injektion.
     * Die drei Services werden hier reingereicht (typisch für Spring Boot)
     */
    public EnergyUsageController(EnergyUsageService energyUsageService, ProducerService producerService, EnergyRecordService energyRecordService) {
        this.energyUsageService = energyUsageService;
        this.producerService = producerService;
        this.energyRecordService = energyRecordService;
    }

    /**
     * GET-Endpunkt: /energy/current-percent
     * Liefert den aktuellen Prozentsatz der Netzabhängigkeit zurück.
     * Antwort ist ein einfacher double-Wert (z.B. 42.5 für 42,5%).
     */
    @GetMapping("/current-percent")
    public double getCurrentGridDependencyPercent() {
        // Berechnet den aktuellen Wert durch den EnergyRecordService
        return energyRecordService.calculateCurrentGridDependencyPercent();
    }

    /**
     * GET-Endpunkt: /energy/all
     * Liefert eine Liste aller EnergyUsage-Einträge zurück.
     * Nützlich, um alle Verbrauchsdaten einzusehen.
     */
    @GetMapping("/all")
    public List<EnergyUsage> getAllUsages() {
        // Holt alle Einträge aus der DB via Service
        return energyUsageService.findAll();
    }

    /**
     * GET-Endpunkt: /energy/current
     * Liefert den aktuellsten (letzten) EnergyUsage-Datensatz zurück.
     * Kann für Live-Anzeigen genutzt werden.
     */
    @GetMapping("/current")
    public EnergyUsage getCurrentUsage() {
        // Holt den neuesten Datensatz (nach Timestamp sortiert)
        return energyUsageService.findLatest();
    }

    /**
     * GET-Endpunkt: /energy/historical?start=...&end=...
     * Liefert alle EnergyUsage-Daten zwischen zwei Zeitpunkten zurück.
     * Parameter:
     *  - start: Startzeitpunkt (ISO 8601 Format, z.B. 2025-06-23T18:00)
     *  - end: Endzeitpunkt
     */
    @GetMapping("/historical")
    public List<EnergyUsage> getHistoricalData(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        // Holt alle Datensätze im angegebenen Zeitbereich via Service
        return energyUsageService.findByTimestampBetween(start, end);
    }

    /**
     * GET-Endpunkt: /energy/send-test-user-message
     * Hilfsmethode zum Testen: Sendet eine Test-Nachricht über RabbitMQ.
     * Gibt einfachen String zurück zur Bestätigung.
     */
    @GetMapping("/send-test-user-message")
    public String sendTestUserMessage() {
        // Ruft die Methode im ProducerService auf, die Testnachricht verschickt
        producerService.sendTestUserMessage();
        return "Test Message Sent";
    }
}
