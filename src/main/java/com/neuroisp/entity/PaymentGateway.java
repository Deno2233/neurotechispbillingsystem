package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_gateways")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name; // PAYHERO

    private String apiUrl;

    private String authorizationToken;

    private String provider; // m-pesa

    private String channelId;

    private boolean active;
}
