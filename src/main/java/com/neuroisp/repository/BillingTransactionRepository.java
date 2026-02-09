package com.neuroisp.repository;

import com.neuroisp.entity.BillingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillingTransactionRepository
        extends JpaRepository<BillingTransaction, String> {


    boolean existsByPaymentReference(String paymentReference);
    List<BillingTransaction> findByUserIdOrderByTransactionTimeAsc(String userId);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM BillingTransaction t
        WHERE t.type = 'PAYMENT'
        AND t.transactionTime BETWEEN :start AND :end
    """)
    double totalRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT DATE(t.transactionTime), SUM(t.amount)
        FROM BillingTransaction t
        WHERE t.type = 'PAYMENT'
        GROUP BY DATE(t.transactionTime)
        ORDER BY DATE(t.transactionTime)
    """)
    List<Object[]> dailyRevenue();
}
