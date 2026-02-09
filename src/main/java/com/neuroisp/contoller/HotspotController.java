package com.neuroisp.contoller;

import com.neuroisp.entity.UserSubscription;
import com.neuroisp.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("api/hotspot")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
       "http://192.168.100.3:4567",
       "http://192.168.15.26:8061"
})
public class HotspotController {

    private final UserSubscriptionService subscriptionService;

    /**
     * MikroTik redirect URL
     * http://yourserver/hotspot/buy?mac=xx&ip=xx
     */
    @GetMapping("/packages")
    public String showPackages() {
        return "REDIRECT_TO_FRONTEND_PAGE";
    }

    /**
     * User selects package
     * Frontend no longer sends MAC/IP
     */
    @PostMapping("/subscribe")
    public UserSubscription subscribe(
            HttpServletRequest request,
            @RequestBody Map<String, String> body
    ) {

        String packageId = body.get("packageId");
        String phone     = body.get("phone");
        String clientIp  = body.get("ip");
        String clientMac = body.get("mac");
        // 🔑 Get the client IP from the HTTP request
       // String clientIp = request.getRemoteAddr();

        // Pass client IP to the service
        return subscriptionService.createSubscription(packageId, phone, clientIp);
    }



}
