package com.neuroisp.service;

import com.neuroisp.repository.BillingTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final BillingTransactionRepository billingRepo;

    public double todayRevenue() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return billingRepo.totalRevenueBetween(start, end);
    }

    public double monthlyRevenue() {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return billingRepo.totalRevenueBetween(start, LocalDateTime.now());
    }

    public double rangeRevenue(LocalDate start, LocalDate end) {
        return billingRepo.totalRevenueBetween(
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );
    }

    public List<Object[]> dailyBreakdown() {
        return billingRepo.dailyRevenue();
    }
}
