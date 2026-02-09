package com.neuroisp.service;

import com.neuroisp.entity.SystemUser;
import com.neuroisp.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .disabled(!user.isActive())
                .build();
    }
}
