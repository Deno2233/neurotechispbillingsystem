package com.neuroisp.contoller;

import com.neuroisp.dto.UpdateSystemUserRequest;
import com.neuroisp.dto.UserProfileUpdateRequest;
import com.neuroisp.entity.Role;
import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import com.neuroisp.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system-users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class SystemUserController {

    private final SystemUserRepository repository;
    private final SystemUserService service;
    private final PasswordEncoder encoder;

    // ✅ CREATE (already discussed)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public SystemUser create(@RequestBody SystemUser user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    // ✅ LIST
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public List<SystemUser> all() {
        return repository.findAll();
    }

    // ✅ UPDATE
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public SystemUser update(
            @PathVariable String id,
            @RequestBody UpdateSystemUserRequest request,
            Authentication authentication
    ) {

        String currentUsername = authentication.getName();

        Role currentRole = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow();

        return service.update(id, request, currentUsername, currentRole);
    }

    // ✅ DELETE
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String id,
            Authentication authentication
    ) {

        String currentUsername = authentication.getName();

        Role currentRole = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow();

        service.delete(id, currentUsername, currentRole);

        return ResponseEntity.ok(
                Map.of("message", "User deleted successfully")
        );
    }
}
