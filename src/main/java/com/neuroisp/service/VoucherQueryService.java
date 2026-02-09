package com.neuroisp.service;



import com.neuroisp.entity.Voucher;
import com.neuroisp.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;

    public List<Voucher> getUnusedVouchers() {
        return voucherRepository.findByUsedFalse();
    }
}
