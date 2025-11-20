package com.tuguna.rating_system.dto.gameobject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameObjectUpdateRequest {
    private String name;
    @Min(value = 0, message = "Price must be a positive number")
    private Double price;
    private Long gameId;
}
