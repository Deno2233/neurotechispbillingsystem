package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mikrotik_routers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MikrotikRouter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String routerName;
    private String ipAddress;

    @Builder.Default
    private int apiPort = 8728;

    private String username;
    private String password;

    @Builder.Default
    private boolean active = true;
}
