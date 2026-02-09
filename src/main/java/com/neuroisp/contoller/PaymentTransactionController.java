package com.neuroisp.contoller;

import com.neuroisp.dto.PaymentTransactionDTO;
import com.neuroisp.entity.PaymentTransaction;
import com.neuroisp.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PaymentTransactionController {

    private final PaymentTransactionRepository repository;

    @GetMapping
    public List<PaymentTransactionDTO> getAllPayments() {

        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PaymentTransactionDTO toDTO(PaymentTransaction tx) {
        return PaymentTransactionDTO.builder()
                .id(tx.getId())
                .mpesaReceiptNumber(tx.getMpesaReceiptNumber())
                .status(tx.getStatus())
                .amount(tx.getAmount())
                .paymentDate(tx.getPaymentDate())
                .build();
    }
}
