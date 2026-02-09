package com.neuroisp.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pppoe_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PppoePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name; // e.g. "10 Mbps Home"

    private String downloadSpeed; // e.g. "10M"
    private String uploadSpeed;   // e.g. "10M"

    private double price; // monthly price

    private boolean active;

    /**
     * MikroTik PPPoE profile name
     */
    private String mikrotikProfile;

    /**
     * Bind package to router
     */
    private String routerId;
}
