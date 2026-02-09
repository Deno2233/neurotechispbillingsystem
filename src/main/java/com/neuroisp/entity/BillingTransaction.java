package com.neuroisp.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "billing_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 12)
    private String transactionCode; // e.g. TX9F3K2A1B

    @ManyToOne
    @JoinColumn(name = "pppoe_user_id", nullable = false)
    private PppoeUser user;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private PppoeSubscription subscription;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // CREDIT / PAYMENT

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CASH / MPESA / SYSTEM

    private Double amount; // positive number always

    private String paymentReference; // Mpesa receipt if applicable

    private String description;

    private LocalDateTime transactionTime;

    private Double balanceAfter; // snapshot after transaction
}
