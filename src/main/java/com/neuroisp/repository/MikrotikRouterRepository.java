package com.neuroisp.repository;

import com.neuroisp.entity.MikrotikRouter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MikrotikRouterRepository extends JpaRepository<MikrotikRouter, String> {
    Optional<MikrotikRouter> findByActiveTrue();
}
