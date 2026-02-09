package com.neuroisp.dto;



import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PppoeUserDetailsDTO {

    // USER
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String username;

    // SUBSCRIPTION
    private String subscriptionId;
    private String packageName;

    private String downloadSpeed;   // ✅ NEW
    private String uploadSpeed;     // ✅ NEW

    private String status;
    private LocalDateTime activationDate;
    private LocalDateTime expiryDate;

    // BILLING
    private double balance;
}
