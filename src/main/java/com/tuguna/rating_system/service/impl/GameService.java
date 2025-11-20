package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.game.GameResponse;
import com.tuguna.rating_system.exception.ApiException;
import com.tuguna.rating_system.exception.ErrorCode;
import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ModelMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    public GameResponse create(String title) {
        if (gameRepository.existsByTitleIgnoreCase(title)) {throw new ApiException(ErrorCode.GAME_TITLE_EXISTS, "Game with this title already exists");
        }
        Game game = Game.builder()
                .title(title)
                .build();

        gameRepository.save(game);
        return toResponse(game);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Transactional
    public GameResponse update(Long id, String newTitle) {

        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.GAME_NOT_FOUND,
                        "Game not found"
                ));

        if (newTitle != null && !newTitle.equals(game.getTitle())) {

            if (gameRepository.existsByTitleIgnoreCase(newTitle)) {
                throw new ApiException(
                        ErrorCode.GAME_TITLE_EXISTS,
                        "Game with this title already exists"
                );
            }

            game.setTitle(newTitle);
        }

        return toResponse(game);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    public void delete(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND, "Game not found"));
        gameRepository.delete(game);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    public List<GameResponse> getAll() {
        return gameRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    public GameResponse getById(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND, "Game not found"));
        return toResponse(game);
    }

    // -----------------------------
    // DTO MAPPER
    // -----------------------------
    private GameResponse toResponse(Game game) {
        return mapper.map(game, GameResponse.class);
    }
}