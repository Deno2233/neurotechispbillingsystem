package com.neuroisp.dto;

import com.neuroisp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String fullName;
    private Role role;
}
