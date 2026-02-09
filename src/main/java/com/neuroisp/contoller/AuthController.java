package com.neuroisp.contoller;

import com.neuroisp.config.JwtUtil;
import com.neuroisp.dto.LoginRequest;
import com.neuroisp.dto.LoginResponse;
import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SystemUserRepository systemUserRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SystemUser user = systemUserRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        user.getUsername(),   // ✅ frontend uses this
                        user.getFullName(),   // 👌 nice for navbar
                        user.getRole()
                )
        );
    }
}
