package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "internet_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternetPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name; // e.g. "1 Hour Unlimited"

    private int durationMinutes; // 60, 1440, etc

    private boolean unlimited; // true

    private String speedLimit; // e.g. "2M/2M", "5M/5M", "0/0" for unlimited

    private double price; // e.g. 10 KES

    private String mikrotikProfile;

    private boolean active;

    /**
     * Bind this package to a specific Mikrotik router
     */
    private String routerId;
    @ManyToOne
    @JoinColumn(name = "isp_company_id")
    private IspCompany ispCompany;

}
