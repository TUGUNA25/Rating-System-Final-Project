package com.tuguna.rating_system.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    @NotNull(message = "Role is required")
    private String role;
}