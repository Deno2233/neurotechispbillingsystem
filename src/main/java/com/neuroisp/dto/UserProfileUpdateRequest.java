package com.neuroisp.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String fullName;
    private String username;         // new username
    private String currentPassword;  // must provide to change password
    private String newPassword;      // optional, new password
}
