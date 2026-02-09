package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Hotspot user credentials
    private String username;
    private String password;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private InternetPackage internetPackage;

    @ManyToOne
    @JoinColumn(name = "router_id")
    private MikrotikRouter router;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime startTime;
    private LocalDateTime expiryTime;

    private String paymentReference; // Mpesa receipt

    private String clientMac; // optional
    private String clientIp;  // optional
    private boolean cleanedUp; // default false

}
