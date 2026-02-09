package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String externalReference; // Subscription ID

    private String mpesaReceiptNumber;

    private String status; // Success / Failed

    private double amount;

    private LocalDateTime paymentDate;
}
