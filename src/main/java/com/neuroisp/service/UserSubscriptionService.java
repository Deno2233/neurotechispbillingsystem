package com.neuroisp.service;

import com.neuroisp.dto.HotspotClient;
import com.neuroisp.entity.*;
import com.neuroisp.repository.*;
import com.neuroisp.scheduler.QuartzSchedulerService;
import com.neuroisp.service.MikrotikService;
import com.neuroisp.sms.SmsService;
import com.neuroisp.sms.SubscriptionSmsBuilder;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {
    private final PayHeroService payHeroService;

    private final UserSubscriptionRepository subscriptionRepo;
    private final InternetPackageRepository packageRepo;
    private final MikrotikRouterRepository routerRepo;
    private final MikrotikService mikrotikService;
    private final SmsService smsService;
    private final SubscriptionSmsBuilder smsBuilder;
    private final IspCompanyService ispCompanyService;
    private final QuartzSchedulerService quartzSchedulerService;

    /**
     * STEP 1: User selects package (before payment)
     * MAC & IP are fetched directly from MikroTik
     */
    public UserSubscription createSubscription(
            String packageId,
            String phoneNumber,
            String clientIp   // comes from HttpServletRequest
    ) {

        InternetPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        MikrotikRouter router = routerRepo.findById(pkg.getRouterId())
                .orElseThrow(() -> new RuntimeException("Router not found"));

        // 🔑 TRUSTED SOURCE: MikroTik hotspot active list
        HotspotClient client =
                mikrotikService.findActiveClientByIp(router, clientIp);

        String username = generateUniqueUsername(phoneNumber);
        String password = generatePassword();

        UserSubscription sub = UserSubscription.builder()
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .internetPackage(pkg)
                .router(router)
                .status(SubscriptionStatus.PENDING)
                .clientMac(client.mac())
                .clientIp(client.ip())
                .build();

        UserSubscription saved = subscriptionRepo.save(sub);

// 🔔 Trigger STK Push (DOES NOT activate yet)


// Trigger PayHero STK Push using package price
        payHeroService.sendStkPush(saved);

        return saved;




    }

    /**
     * STEP 2: After payment confirmation
     */
    public void activateSubscription(String subscriptionId, String paymentRef) {

        UserSubscription sub = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        InternetPackage pkg = sub.getInternetPackage();

        sub.setPaymentReference(paymentRef);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setStartTime(LocalDateTime.now());
        sub.setExpiryTime(
                LocalDateTime.now().plusMinutes(pkg.getDurationMinutes())
        );

        createHotspotUser(sub);

        // 🔹 Auto-login using username, password, MAC, and IP
        autoLoginHotspotUser(sub);

        UserSubscription saved = subscriptionRepo.save(sub);
        // ⭐ Schedule Quartz expiry
        try {
            quartzSchedulerService.scheduleSubscriptionExpiry(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to schedule expiry job", e);
        }

        // ✅ SEND SMS AFTER SUCCESSFUL ACTIVATION
        // 📩 Notify user
        try {
            IspCompany isp = ispCompanyService.getActiveCompany();
            String sms = smsBuilder.build(sub ,isp);
            smsService.send(isp, sub.getPhoneNumber(), sms);
        } catch (Exception ignored) {}

    }

    /**
     * STEP 3: Create & bind user on MikroTik
     */
    private void createHotspotUser(UserSubscription sub) {

        try (ApiConnection con = mikrotikService.connect(sub.getRouter())) {

            // Check if user already exists
            boolean exists = con.execute("/ip/hotspot/user/print")
                    .stream()
                    .anyMatch(u -> sub.getUsername().equals(u.get("name")));

            if (exists) {
                // Ensure MAC is bound (reconnect safety)
                if (sub.getClientMac() != null) {
                    con.execute(String.format(
                            "/ip/hotspot/user/set [find name=%s] mac-address=%s",
                            sub.getUsername(),
                            sub.getClientMac()
                    ));
                }
                return;
            }

            String macPart = sub.getClientMac() != null
                    ? " mac-address=" + sub.getClientMac()
                    : "";

            String command = String.format(
                    "/ip/hotspot/user/add name=%s password=%s profile=%s limit-uptime=%s%s",
                    sub.getUsername(),
                    sub.getPassword(),
                    sub.getInternetPackage().getMikrotikProfile(),
                    sub.getInternetPackage().getDurationMinutes() + "m",
                    macPart
            );

            con.execute(command);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create hotspot user", e);
        }
    }

    /**
     * Auto-login using username, password, MAC, and IP
     */
    private void autoLoginHotspotUser(UserSubscription sub) {
        if (sub.getClientMac() == null || sub.getClientIp() == null) return;

        try (ApiConnection con = mikrotikService.connect(sub.getRouter())) {

            String command = String.format(
                    "/ip/hotspot/active/login user=%s password=%s mac-address=%s ip=%s",
                    sub.getUsername(),
                    sub.getPassword(),
                    sub.getClientMac(),
                    sub.getClientIp()
            );

            con.execute(command);

        } catch (Exception e) {
            throw new RuntimeException("Failed to auto-login hotspot user", e);
        }
    }

    /**
     * Generate short unique username (max 6 chars, lowercase)
     * Example: hs9k3a
     */
    private String generateUniqueUsername(String phone) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("hs");

        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }

        return sb.toString();
    }


    private String generatePassword() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }
}
