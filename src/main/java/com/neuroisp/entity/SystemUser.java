package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private Role role; // SUPER_ADMIN, ADMIN, SUPPORT

}
