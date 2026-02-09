package com.neuroisp.contoller;

import com.neuroisp.entity.SmsProvider;
import com.neuroisp.service.SmsProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sms-providers")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class SmsProviderController {

    private final SmsProviderService smsProviderService;

    @PostMapping
    public SmsProvider create(@RequestBody SmsProvider provider) {
        return smsProviderService.create(provider);
    }

    @PutMapping("/{id}")
    public SmsProvider update(
            @PathVariable String id,
            @RequestBody SmsProvider provider
    ) {
        return smsProviderService.update(id, provider);
    }

    @GetMapping
    public List<SmsProvider> getAll() {
        return smsProviderService.findAll();
    }

    @GetMapping("/{id}")
    public SmsProvider getOne(@PathVariable String id) {
        return smsProviderService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        smsProviderService.delete(id);
    }
}
