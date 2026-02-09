package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String code; // PIN

    @ManyToOne
    private InternetPackage internetPackage;

    private boolean used;

    private LocalDateTime usedAt;
    private String macAddress; // for MAC binding

}
