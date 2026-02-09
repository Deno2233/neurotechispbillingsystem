package com.neuroisp.service;

import com.neuroisp.entity.InternetPackage;
import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.repository.InternetPackageRepository;
import com.neuroisp.repository.MikrotikRouterRepository;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternetPackageService {

    private final InternetPackageRepository repository;
    private final MikrotikRouterRepository routerRepository;
    private final MikrotikService mikrotikService;

    /**
     * Create package AND Mikrotik hotspot profile
     */
    public InternetPackage createPackage(InternetPackage pkg, String routerId) {

        // Fetch router
        MikrotikRouter router = routerRepository.findById(routerId)
                .orElseThrow(() -> new RuntimeException("Router not found"));

        // Create profile on Mikrotik
        createHotspotProfile(router, pkg);

        // Save package in DB with routerId
        pkg.setActive(true);
        pkg.setRouterId(routerId);
        return repository.save(pkg);
    }

    /**
     * Update package AND sync Mikrotik hotspot profile
     */
    public InternetPackage updatePackage(String id, InternetPackage updated) {

        InternetPackage existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        // Prevent renaming Mikrotik profile
        if (!existing.getMikrotikProfile().equals(updated.getMikrotikProfile())) {
            throw new RuntimeException("Mikrotik profile name cannot be changed");
        }

        // Get router
        MikrotikRouter router = routerRepository.findById(existing.getRouterId())
                .orElseThrow(() -> new RuntimeException("Router not found"));

        // Update profile on Mikrotik
        updateHotspotProfile(router, updated);

        // Update DB fields
        existing.setName(updated.getName());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setUnlimited(updated.isUnlimited());
        existing.setSpeedLimit(updated.getSpeedLimit());
        existing.setPrice(updated.getPrice());

        return repository.save(existing);
    }

    /**
     * List all packages
     */
    public List<InternetPackage> getAllPackages() {
        return repository.findAll();
    }

    /**
     * Deactivate package (soft delete)
     */
    public void deactivatePackage(String id) {
        InternetPackage pkg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        pkg.setActive(false);
        repository.save(pkg);
    }

    /**
     * Create Mikrotik hotspot profile
     */
    private void createHotspotProfile(MikrotikRouter router, InternetPackage pkg) {
        try (ApiConnection con = mikrotikService.connect(router)) {

            boolean exists = con.execute("/ip/hotspot/user/profile/print")
                    .stream()
                    .anyMatch(row -> pkg.getMikrotikProfile().equals(row.get("name")));

            if (exists) return;

            String command = String.format(
                    "/ip/hotspot/user/profile/add name=%s rate-limit=%s session-timeout=%s shared-users=1",
                    pkg.getMikrotikProfile(),
                    resolveRateLimit(pkg),
                    resolveSessionTimeout(pkg)
            );

            con.execute(command);

        } catch (MikrotikApiException e) {
            throw new RuntimeException("Failed to create Mikrotik profile", e);
        }
    }

    /**
     * Update Mikrotik hotspot profile
     */
    private void updateHotspotProfile(MikrotikRouter router, InternetPackage pkg) {
        try (ApiConnection con = mikrotikService.connect(router)) {

            String command = String.format(
                    "/ip/hotspot/user/profile/set numbers=%s rate-limit=%s session-timeout=%s",
                    pkg.getMikrotikProfile(),
                    resolveRateLimit(pkg),
                    resolveSessionTimeout(pkg)
            );

            con.execute(command);

        } catch (MikrotikApiException e) {
            throw new RuntimeException("Failed to update Mikrotik profile", e);
        }
    }

    /**
     * Helper: calculate rate limit
     */
    private String resolveRateLimit(InternetPackage pkg) {
        return pkg.isUnlimited() ? "0/0" : pkg.getSpeedLimit();
    }

    /**
     * Helper: calculate session timeout
     */
    private String resolveSessionTimeout(InternetPackage pkg) {
        return pkg.getDurationMinutes() > 0
                ? pkg.getDurationMinutes() + "m"
                : "0";
    }
}
