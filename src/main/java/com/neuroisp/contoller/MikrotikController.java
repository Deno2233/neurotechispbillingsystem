package com.neuroisp.contoller;

import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.repository.MikrotikRouterRepository;
import com.neuroisp.service.MikrotikService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mikrotik")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class MikrotikController {

    private final MikrotikService mikrotikService;
    private final MikrotikRouterRepository routerRepository;

    /**
     * Test connection to a Mikrotik router
     */
    @PostMapping("/test")
    public ResponseEntity<?> testConnection(@RequestBody Map<String, String> request) {
        String ip = request.get("ipAddress");
        int port = Integer.parseInt(request.getOrDefault("apiPort", "8728"));
        String username = request.get("username");
        String password = request.get("password");

        boolean success = mikrotikService.testConnection(ip, port, username, password);
        if (success)
            return ResponseEntity.ok(Map.of("success", true, "message", "Connection successful!"));
        else
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Connection failed."));
    }

    /**
     * Save a router to the database
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveRouter(@RequestBody MikrotikRouter router) {
        MikrotikRouter saved = routerRepository.save(router);
        return ResponseEntity.ok(saved);
    }

    /**
     * List all routers
     */
    @GetMapping("/routers")
    public ResponseEntity<List<MikrotikRouter>> listRouters() {
        return ResponseEntity.ok(routerRepository.findAll());
    }

    // 🆕 UPDATE ROUTER
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRouter(
            @PathVariable String id,
            @RequestBody MikrotikRouter updated
    ) {
        return routerRepository.findById(id)
                .map(existing -> {
                    existing.setRouterName(updated.getRouterName());
                    existing.setIpAddress(updated.getIpAddress());
                    existing.setApiPort(updated.getApiPort());
                    existing.setUsername(updated.getUsername());
                    existing.setPassword(updated.getPassword());
                    existing.setActive(updated.isActive());

                    return ResponseEntity.ok(routerRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🆕 DELETE ROUTER
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRouter(@PathVariable String id) {
        if (!routerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        routerRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Router deleted successfully"));
    }

}
