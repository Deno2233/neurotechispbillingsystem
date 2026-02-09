package com.neuroisp.service;

import com.neuroisp.entity.PaymentTransaction;
import com.neuroisp.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository repo;

    public void logTransaction(
            String externalRef,
            String receipt,
            String status,
            double amount
    ) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .externalReference(externalRef)
                .mpesaReceiptNumber(receipt)
                .status(status)
                .amount(amount)
                .paymentDate(LocalDateTime.now())
                .build();

        repo.save(tx);
    }
}
