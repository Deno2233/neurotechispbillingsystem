package com.neuroisp.contoller;



import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SystemUserRepository userRepository;

    // ✅ Return current logged-in user info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName(); // comes from JWT
        SystemUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove password from response
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }
}
