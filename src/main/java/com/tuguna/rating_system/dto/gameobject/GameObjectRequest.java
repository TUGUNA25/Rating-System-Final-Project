package com.tuguna.rating_system.dto.gameobject;

import lombok.Data;

@Data
public class GameObjectRequest {

    private String name;
    private Double price;
    private Long gameId;
}