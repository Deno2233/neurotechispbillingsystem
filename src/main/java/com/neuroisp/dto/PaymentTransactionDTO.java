package com.neuroisp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransactionDTO {

    private String id;
    private String mpesaReceiptNumber;
    private String status;
    private double amount;
    private LocalDateTime paymentDate;
}
