package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pppoe_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PppoeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;          // REQUIRED

    private String phoneNumber;

    private String address;        // OPTIONAL
    private String location;       // OPTIONAL

    @Column(unique = true)
    private String username;       // PPPoE username

    private String password;       // PPPoE password

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "router_id")
    private MikrotikRouter router;
}
