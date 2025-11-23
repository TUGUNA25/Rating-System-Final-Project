package com.tuguna.rating_system.dto.gameobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameObjectResponse {
    private Long id;
    private String name;
    private Double price;

    private Long sellerId;
    private String sellerFirstName;
    private String sellerLastName;

    private Long gameId;
    private String gameTitle;

    private String createdAt;
}