package com.neuroisp.repository;

import com.neuroisp.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, String> {
    Optional<SystemUser> findByUsername(String username);
}
