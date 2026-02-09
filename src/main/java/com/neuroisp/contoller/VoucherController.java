package com.neuroisp.contoller;

import com.neuroisp.entity.UserSubscription;
import com.neuroisp.service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/hotspot/voucher")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class VoucherController {

    private final VoucherService voucherService;

    /**
     * Redeem a voucher
     * Frontend no longer sends MAC/IP
     */
    @PostMapping("/redeem")
    public UserSubscription redeem(
            HttpServletRequest request,
            @RequestBody Map<String, String> body
    ) {

        String pin = body.get("pin");
        String clientMac = body.get("mac");
        String clientIp  = body.get("ip");
        // 🔑 Get client IP from HTTP request
        //String clientIp = request.getRemoteAddr();

        // Redeem voucher with trusted MikroTik MAC/IP
        return voucherService.redeemVoucher(pin, clientIp);
    }
}
