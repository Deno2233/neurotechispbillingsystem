package com.neuroisp.service;

import com.neuroisp.entity.*;
import com.neuroisp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service

@RequiredArgsConstructor
public class BulkVoucherService {

    private final VoucherRepository voucherRepo;
    private final InternetPackageRepository packageRepo;

    public List<Voucher> generateBulk(String packageId, int quantity) {

        InternetPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        List<Voucher> vouchers = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            vouchers.add(
                    Voucher.builder()
                            .code(generatePin())
                            .internetPackage(pkg)
                            .used(false)
                            .build()
            );
        }

        return voucherRepo.saveAll(vouchers);
    }

    private String generatePin() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
    public List<Voucher> getVouchersByPackage(String packageId) {
        InternetPackage pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        return voucherRepo.findByInternetPackage(pkg);
    }

}
