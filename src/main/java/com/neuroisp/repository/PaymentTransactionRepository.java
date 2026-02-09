package com.neuroisp.repository;

import com.neuroisp.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM PaymentTransaction p
        WHERE p.status = 'Success'
        AND p.paymentDate BETWEEN :start AND :end
    """)
    double sumSuccessfulPaymentsBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
