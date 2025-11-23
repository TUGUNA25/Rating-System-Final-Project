package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.game.GameResponse;
import com.tuguna.rating_system.service.impl.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * Get all games
     */
    @GetMapping
    public ApiResponse<List<GameResponse>> getAll() {
        return ApiResponse.success("Games fetched successfully", gameService.getAll());
    }

    /**
     * Get game by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<GameResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Game fetched successfully", gameService.getById(id));
    }
}
