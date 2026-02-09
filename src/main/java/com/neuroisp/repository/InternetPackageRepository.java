package com.neuroisp.repository;

import com.neuroisp.entity.InternetPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternetPackageRepository
        extends JpaRepository<InternetPackage, String> {
}
