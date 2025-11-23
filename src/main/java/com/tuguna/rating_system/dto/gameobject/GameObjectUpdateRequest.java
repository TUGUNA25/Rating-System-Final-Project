package com.tuguna.rating_system.dto.gameobject;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GameObjectUpdateRequest {
    private String name;
    @Min(value = 0, message = "Price must be a positive number")
    private Double price;
    private Long gameId;
}
