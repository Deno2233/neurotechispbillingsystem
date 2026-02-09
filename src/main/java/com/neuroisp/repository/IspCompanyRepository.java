package com.neuroisp.repository;

import com.neuroisp.entity.IspCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IspCompanyRepository extends JpaRepository<IspCompany, String> {
  // Optional<Object> findByActiveTrue();
    Optional<IspCompany> findByActiveTrue();
    List<IspCompany> findAllByActiveTrue();
}
