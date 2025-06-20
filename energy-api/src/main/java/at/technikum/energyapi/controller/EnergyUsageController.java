package at.technikum.energyapi.controller;


import at.technikum.energyapi.EnergyUsage;
import at.technikum.energyapi.repository.EnergyUsageRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/energy")
public class EnergyUsageController {

    private final EnergyUsageRepository repository;

    public EnergyUsageController(EnergyUsageRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/add")
    public EnergyUsage addUsage(@RequestBody EnergyUsage energyUsage) {
        return repository.save(energyUsage);
    }

    @GetMapping("/all")
    public List<EnergyUsage> getAllUsages() {
        return repository.findAll();

    }

}
