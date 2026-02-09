package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pppoe_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PppoeSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private PppoeUser user;

    @ManyToOne
    @JoinColumn(name = "pppoe_package_id")
    private PppoePackage pppoePackage;

    @ManyToOne
    @JoinColumn(name = "router_id")
    private MikrotikRouter router;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime startTime;
    private LocalDateTime expiryTime;

    private String paymentReference; // Mpesa receipt


}
