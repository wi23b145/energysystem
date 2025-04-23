package at.technikum.energyapi;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    // Gibt aktuelle (simulierte) Daten für eine Energiegemeinschaft zurück
    @GetMapping("/current")
    public EnergyUsage getCurrentEnergyUsage() {
        return new EnergyUsage(
                "Energiegemeinschaft Linz",
                135.4,
                120.3,
                LocalDateTime.now().toString()
        );
    }

    // Gibt eine Liste historischer (simulierter) Daten zurück
    @GetMapping("/history")
    public List<EnergyUsage> getHistoricEnergyUsage() {
        return Arrays.asList(
                new EnergyUsage("Energiegemeinschaft Linz", 100.0, 85.0, "2025-04-22T08:00:00"),
                new EnergyUsage("Energiegemeinschaft Linz", 120.5, 105.4, "2025-04-22T09:00:00"),
                new EnergyUsage("Energiegemeinschaft Linz", 135.4, 120.3, "2025-04-22T10:00:00")
        );
    }
}
