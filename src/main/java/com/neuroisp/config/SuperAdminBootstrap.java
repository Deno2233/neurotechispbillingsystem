package com.neuroisp.config;

import com.neuroisp.entity.Role;
import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminBootstrap implements CommandLineRunner {

    private final SystemUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        boolean exists = repository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.SUPER_ADMIN);

        if (!exists) {
            SystemUser superAdmin = SystemUser.builder()
                    .username("superadmin")
                    .password(passwordEncoder.encode("SuperSecret123!"))
                    .fullName("Super Admin")
                    .role(Role.SUPER_ADMIN)
                    .active(true)
                    .build();

            repository.save(superAdmin);
            System.out.println("Super Admin created: username=superadmin, password=SuperSecret123!");
        }
    }
}
