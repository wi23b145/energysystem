package at.technikum.energyapi;
import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.service.EnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {    // Gibt aktuelle (simulierte) Daten für eine Energiegemeinschaft zurück.

    @Autowired
    private EnergyService energyService;

    @PostMapping("/record/add")
    public ResponseEntity<EnergyRecord> addRecord(@RequestBody EnergyRecord record) {
        EnergyRecord saved = energyService.save(record);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/current")
    public EnergyUsage getCurrentEnergyUsage() {
        return new EnergyUsage(                "Energiegemeinschaft Linz",                135.4,                120.3,                LocalDateTime.now().toString()        );    }    // Gibt eine Liste historischer (simulierter) Daten zurück

    @GetMapping("/historical")
    public List<EnergyUsage> getHistoricEnergyUsage(
            @RequestParam String start,
            @RequestParam String end) {        // Du kannst die Parameter optional nutzen oder einfach ignorieren
             return Arrays.asList(
                     new EnergyUsage("Energiegemeinschaft Linz", 100.0, 85.0, start),
                     new EnergyUsage("Energiegemeinschaft Linz", 120.5, 105.4, end)        );
    }

    @GetMapping("/status")
    public String getStatus() {
        return "API läuft!";
    }

}