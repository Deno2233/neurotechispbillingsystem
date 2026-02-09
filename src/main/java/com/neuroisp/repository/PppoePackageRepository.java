package com.neuroisp.repository;

import com.neuroisp.entity.PppoePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PppoePackageRepository extends JpaRepository<PppoePackage, String> {

    List<PppoePackage> findByActiveTrue();
}
