package com.neuroisp.repository;

import com.neuroisp.entity.InternetPackage;
import com.neuroisp.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, String> {

    Optional<Voucher> findByCode(String code);
    List<Voucher> findByInternetPackage(InternetPackage internetPackage);
    List<Voucher> findByUsedFalse();   // ✅ NEW
    Optional<Voucher> findById(String id);

}
