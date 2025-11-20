package com.tuguna.rating_system.dto.game;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameRequest {
    @NotBlank(message = "Game title is required")
    private String title;
}