package com.neuroisp.contoller;

import com.neuroisp.entity.InternetPackage;
import com.neuroisp.service.InternetPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class InternetPackageController {

    private final InternetPackageService service;

    /**
     * Create package AND Mikrotik profile
     * Example:
     * POST /api/admin/packages?routerId=abc123
     */
    @PostMapping
    public InternetPackage create(
            @RequestParam String routerId,
            @RequestBody InternetPackage pkg
    ) {
        return service.createPackage(pkg, routerId);
    }

    /**
     * List all packages
     */
    @GetMapping
    public List<InternetPackage> getAll() {
        return service.getAllPackages();
    }

    /**
     * Update package (DB only)
     */
    @PutMapping("/{id}")
    public InternetPackage update(
            @PathVariable String id,
            @RequestBody InternetPackage pkg
    ) {
        return service.updatePackage(id, pkg);
    }

    /**
     * Soft delete (deactivate)
     */
    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable String id) {
        service.deactivatePackage(id);
    }
}
