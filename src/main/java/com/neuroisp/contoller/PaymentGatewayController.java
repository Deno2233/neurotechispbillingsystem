package com.neuroisp.contoller;

import com.neuroisp.entity.PaymentGateway;
import com.neuroisp.repository.PaymentGatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/payment-gateway")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PaymentGatewayController {

    private final PaymentGatewayRepository repo;

    @GetMapping
    public List<PaymentGateway> list() {
        return repo.findAll();
    }

    @PostMapping
    public PaymentGateway save(@RequestBody PaymentGateway g) {
        return repo.save(g);
    }

    @PutMapping("/{id}")
    public PaymentGateway update(@PathVariable String id, @RequestBody PaymentGateway g) {
        g.setId(id);
        return repo.save(g);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repo.deleteById(id);
    }
}
