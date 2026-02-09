package com.neuroisp.dto;



import com.neuroisp.entity.Role;
import lombok.Data;

@Data
public class UpdateSystemUserRequest {
    private String fullName;
    private String password; // optional
    private Role role;
    private Boolean active;
}
