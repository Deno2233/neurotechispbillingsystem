package com.neuroisp.dto;

import com.neuroisp.entity.SubscriptionStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PppoeUserViewDTO {

    private String userId;
    private String username;
    private String fullName;
    private String phoneNumber;

    private String packageName;     // from PppoeSubscription
    private SubscriptionStatus status; // from PppoeSubscription
}
