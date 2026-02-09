package com.neuroisp.service;


import com.neuroisp.entity.Voucher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherExportService {

    public String exportCsv(List<Voucher> vouchers) {

        StringBuilder csv = new StringBuilder("PIN,PACKAGE,USED\n");

        for (Voucher v : vouchers) {
            csv.append(v.getCode())
                    .append(",")
                    .append(v.getInternetPackage().getName())
                    .append(",")
                    .append(v.isUsed())
                    .append("\n");
        }
        return csv.toString();
    }
}
