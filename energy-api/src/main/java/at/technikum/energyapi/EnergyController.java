package at.technikum.energyapi;
import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @PostMapping("/record/add")
    public ResponseEntity<EnergyRecord> addRecord(@RequestBody EnergyRecord record) {
        EnergyRecord saved = energyService.save(record);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/current")
    public EnergyUsage getCurrentEnergyUsage() {
        EnergyRecord latest=energyService.getLatestRecord();
        return new EnergyUsage(
                latest.getCommunityName(),
                latest.getProductionKw(),
                latest.getConsumptionKw(),
                latest.getTimestamp()
        );
    }    // Gibt eine Liste historischer (simulierter) Daten zurück

    @GetMapping("/historical")
    public List<EnergyUsage> getHistoricEnergyUsage(@RequestParam String start, @RequestParam String end) {
        List<EnergyRecord> records = energyService.getAllRecords();
        return records.stream()
                .map(r -> new EnergyUsage(
                        r.getCommunityName(),
                        r.getProductionKw(),
                        r.getConsumptionKw(),
                        r.getTimestamp()
                ))
                .toList();
    }

    @GetMapping("/status")
    public String getStatus() {
        return "API läuft!";
    }

}