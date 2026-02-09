package com.neuroisp.service;

import com.neuroisp.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentSummaryService {

    private final PaymentTransactionRepository repository;

    public double todayTotal() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return repository.sumSuccessfulPaymentsBetween(start, end);
    }

    public double weeklyTotal() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek =
                today.with(DayOfWeek.MONDAY);

        return repository.sumSuccessfulPaymentsBetween(
                startOfWeek.atStartOfDay(),
                LocalDateTime.now()
        );
    }

    public double monthlyTotal() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth =
                today.withDayOfMonth(1);

        return repository.sumSuccessfulPaymentsBetween(
                startOfMonth.atStartOfDay(),
                LocalDateTime.now()
        );
    }
}
