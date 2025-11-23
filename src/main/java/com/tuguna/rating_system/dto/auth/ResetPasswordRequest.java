package com.tuguna.rating_system.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset code is required")
    private String code;

    @NotBlank(message = "New password is required")
    private String newPassword;
}