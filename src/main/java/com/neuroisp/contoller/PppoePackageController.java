package com.neuroisp.contoller;

import com.neuroisp.entity.PppoePackage;
import com.neuroisp.service.PppoePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pppoe-packages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PppoePackageController {

    private final PppoePackageService service;

    @PostMapping("/{routerId}")
    public PppoePackage create(
            @PathVariable String routerId,
            @RequestBody PppoePackage pkg
    ) {
        return service.create(pkg, routerId);
    }

    @PutMapping("/{id}")
    public PppoePackage update(
            @PathVariable String id,
            @RequestBody PppoePackage pkg
    ) {
        return service.update(id, pkg);
    }

    @GetMapping
    public List<PppoePackage> list() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable String id) {
        service.deactivate(id);
    }
}
