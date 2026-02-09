package com.neuroisp.contoller;

import com.neuroisp.dto.WalletTopUpRequest;
import com.neuroisp.service.PayHeroWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payhero")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PayHeroController {

    private final PayHeroWalletService payHeroWalletService;

    // --- Get Wallet Balance ---
    @GetMapping("/wallet/balance")
    public String getWalletBalance() throws Exception {
        return payHeroWalletService.getWalletBalance();
    }

    // --- Top-Up Wallet ---
    @PostMapping("/wallet/topup")
    public String topUpWallet(@RequestBody WalletTopUpRequest request) throws Exception {
        return payHeroWalletService.topUpWallet(request.getPhoneNumber(), request.getAmount());
    }
}
