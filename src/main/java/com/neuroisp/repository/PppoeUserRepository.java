package com.neuroisp.repository;

import com.neuroisp.entity.PppoeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PppoeUserRepository extends JpaRepository<PppoeUser, String> {
    Optional<PppoeUser> findByEmail(String email);
    Optional<PppoeUser> findByUsername(String username);

    Optional<Object> findByPhoneNumber(String phone);
}
