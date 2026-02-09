package com.neuroisp.repository;

import com.neuroisp.entity.SmsProvider;
import com.neuroisp.entity.SmsProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsProviderRepository extends JpaRepository<SmsProvider, String> {
    Optional<SmsProvider> findByName(String name);

   // Optional<SmsProvider> findByTypeAndActiveTrue(SmsProviderType type);
    Optional<SmsProvider> findByNameAndActiveTrue(String name);
    Optional<SmsProvider> findByNameAndActive(String name, boolean active);
}
