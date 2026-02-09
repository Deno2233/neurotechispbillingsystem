package com.neuroisp.service;

import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.entity.PppoePackage;
import com.neuroisp.repository.MikrotikRouterRepository;
import com.neuroisp.repository.PppoePackageRepository;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PppoePackageService {

    private final PppoePackageRepository repository;
    private final MikrotikRouterRepository routerRepository;
    private final MikrotikService mikrotikService;

    /**
     * Create PPPoE package + MikroTik profile
     */
    public PppoePackage create(PppoePackage pkg, String routerId) {

        MikrotikRouter router = routerRepository.findById(routerId)
                .orElseThrow(() -> new RuntimeException("Router not found"));

        createPppoeProfile(router, pkg);

        pkg.setRouterId(routerId);
        pkg.setActive(true);
        return repository.save(pkg);
    }

    /**
     * Update PPPoE package
     */
    public PppoePackage update(String id, PppoePackage updated) {

        PppoePackage existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        if (!existing.getMikrotikProfile().equals(updated.getMikrotikProfile())) {
            throw new RuntimeException("PPPoE profile name cannot be changed");
        }

        MikrotikRouter router = routerRepository.findById(existing.getRouterId())
                .orElseThrow(() -> new RuntimeException("Router not found"));

        updatePppoeProfile(router, updated);

        existing.setName(updated.getName());
        existing.setDownloadSpeed(updated.getDownloadSpeed());
        existing.setUploadSpeed(updated.getUploadSpeed());
        existing.setPrice(updated.getPrice());

        return repository.save(existing);
    }

    public List<PppoePackage> getAll() {
        return repository.findAll();
    }

    public void deactivate(String id) {
        PppoePackage pkg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        pkg.setActive(false);
        repository.save(pkg);
    }

    /**
     * MikroTik PPPoE profile creation
     */
    private void createPppoeProfile(MikrotikRouter router, PppoePackage pkg) {

        try (ApiConnection con = mikrotikService.connect(router)) {

            boolean exists = con.execute("/ppp/profile/print")
                    .stream()
                    .anyMatch(r -> pkg.getMikrotikProfile().equals(r.get("name")));

            if (exists) return;

            String rateLimit = pkg.getUploadSpeed() + "/" + pkg.getDownloadSpeed();

            con.execute(String.format(
                    "/ppp/profile/add name=%s rate-limit=%s only-one=yes",
                    pkg.getMikrotikProfile(),
                    rateLimit
            ));

        } catch (MikrotikApiException e) {
            throw new RuntimeException("Failed to create PPPoE profile", e);
        }
    }

    /**
     * Update PPPoE profile
     */
    private void updatePppoeProfile(MikrotikRouter router, PppoePackage pkg) {

        try (ApiConnection con = mikrotikService.connect(router)) {

            String rateLimit = pkg.getUploadSpeed() + "/" + pkg.getDownloadSpeed();

            con.execute(String.format(
                    "/ppp/profile/set name=%s rate-limit=%s",
                    pkg.getMikrotikProfile(),
                    rateLimit
            ));

        } catch (MikrotikApiException e) {
            throw new RuntimeException("Failed to update PPPoE profile", e);
        }
    }

}
