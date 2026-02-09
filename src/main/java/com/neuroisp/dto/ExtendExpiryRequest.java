package com.neuroisp.dto;



import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExtendExpiryRequest {
    private LocalDateTime newExpiry;
}
