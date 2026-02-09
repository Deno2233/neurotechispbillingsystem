package com.neuroisp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sms_providers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;              // BYTEWAVE, AFRICASTALKING, etc
    private String apiUrl;
    private String apiKey;
    private String senderId;
    private boolean active;
}
