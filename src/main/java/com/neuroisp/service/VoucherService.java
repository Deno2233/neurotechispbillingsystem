package com.neuroisp.service;

import com.neuroisp.dto.HotspotClient;
import com.neuroisp.entity.*;
import com.neuroisp.repository.InternetPackageRepository;
import com.neuroisp.repository.MikrotikRouterRepository;
import com.neuroisp.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepo;
    private final InternetPackageRepository packageRepo;
    private final UserSubscriptionService subscriptionService;
    private final MikrotikRouterRepository routerRepo;   // NEW
    private final MikrotikService mikrotikService;
    /**
     * Admin creates voucher
     */
    public Voucher createVoucher(String packageId) {

        InternetPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        return voucherRepo.save(
                Voucher.builder()
                        .code(generatePin())
                        .internetPackage(pkg)
                        .used(false)
                        .build()
        );
    }

    /**
     * User redeems voucher
     * No MAC/IP from frontend, we fetch from MikroTik
     */
    public UserSubscription redeemVoucher(String pin, String clientIp) {

        Voucher voucher = voucherRepo.findByCode(pin)
                .orElseThrow(() -> new RuntimeException("Invalid voucher"));

        if (voucher.isUsed()) {
            throw new RuntimeException("Voucher already used");
        }

        // 🔑 Get router from InternetPackage
        MikrotikRouter router = routerRepo.findById(
                voucher.getInternetPackage().getRouterId()
        ).orElseThrow(() -> new RuntimeException("Router not found"));

        // 🔑 Get MAC/IP from MikroTik
        HotspotClient client = mikrotikService.findActiveClientByIp(router, clientIp);

        // Check if voucher is locked to a device
        if (voucher.getMacAddress() != null &&
                !voucher.getMacAddress().equals(client.mac())) {
            throw new RuntimeException("Voucher locked to another device");
        }

        // Create subscription
        UserSubscription sub = subscriptionService.createSubscription(
                voucher.getInternetPackage().getId(),
                "VOUCHER",
                clientIp
        );

        // Activate subscription
        subscriptionService.activateSubscription(
                sub.getId(),
                "VOUCHER-" + voucher.getCode()
        );

        // Mark voucher used
        voucher.setUsed(true);
        voucher.setUsedAt(LocalDateTime.now());
        voucher.setMacAddress(client.mac());

        voucherRepo.save(voucher);

        return sub;
    }


    private String generatePin() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
