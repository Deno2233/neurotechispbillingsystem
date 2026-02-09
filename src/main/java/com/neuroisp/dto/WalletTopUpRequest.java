package com.neuroisp.dto;

import lombok.Data;

@Data
public class WalletTopUpRequest {
    private String phoneNumber;
    private double amount;
}
