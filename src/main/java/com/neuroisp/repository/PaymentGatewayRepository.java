package com.neuroisp.repository;

import com.neuroisp.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, String> {

    Optional<PaymentGateway> findByActiveTrue();
    Optional<PaymentGateway> findByNameAndActive(String name, boolean active);
}
