package com.tuguna.rating_system.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Long id;
    private String title;
}