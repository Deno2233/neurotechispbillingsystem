package com.neuroisp.service;



import com.neuroisp.dto.UpdateSystemUserRequest;
import com.neuroisp.entity.Role;
import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemUserService {

    private final SystemUserRepository repository;
    private final PasswordEncoder encoder;

    public SystemUser update(
            String id,
            UpdateSystemUserRequest req,
            String currentUsername,
            Role currentRole
    ) {

        SystemUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❌ prevent ADMIN downgrading / editing SUPER_ADMIN
        if (user.getRole() == Role.SUPER_ADMIN && currentRole != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can modify SUPER_ADMIN");
        }

        if (req.getFullName() != null)
            user.setFullName(req.getFullName());

        if (req.getActive() != null)
            user.setActive(req.getActive());

        if (req.getRole() != null)
            user.setRole(req.getRole());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(encoder.encode(req.getPassword()));
        }

        return repository.save(user);
    }

    public void delete(
            String id,
            String currentUsername,
            Role currentRole
    ) {

        SystemUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❌ cannot delete yourself
        if (user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You cannot delete your own account");
        }

        // ❌ only SUPER_ADMIN deletes SUPER_ADMIN
        if (user.getRole() == Role.SUPER_ADMIN && currentRole != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only SUPER_ADMIN can delete SUPER_ADMIN");
        }

        repository.delete(user);
    }
}
