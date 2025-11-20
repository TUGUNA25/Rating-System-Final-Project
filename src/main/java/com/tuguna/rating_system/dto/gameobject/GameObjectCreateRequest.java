package com.tuguna.rating_system.dto.gameobject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameObjectCreateRequest {
    @NotBlank(message = "Object name is required")
    private String name;
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be a positive number")
    private Double price;
    @NotNull(message = "Game ID is required")
    private Long gameId;
}