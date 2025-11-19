package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.gameobject.GameObjectRequest;
import com.tuguna.rating_system.dto.gameobject.GameObjectResponse;
import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.repository.GameObjectRepository;
import com.tuguna.rating_system.repository.GameRepository;
import com.tuguna.rating_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameObjectService {
    private final GameObjectRepository gameObjectRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    public GameObjectResponse create(GameObjectRequest request) {

        Long sellerId = userService.getCurrentUser().getId();

        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(request.getGameId()).orElseThrow(() -> new RuntimeException("Game not found"));

        User sellerRef = User.builder().id(sellerId).build();

        GameObject object = GameObject.builder()
                .name(request.getName())
                .price(request.getPrice())
                .seller(seller)
                .game(game)
                .build();

        gameObjectRepository.save(object);

        return toResponse(object);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Transactional
    public GameObjectResponse update(Long id, GameObjectRequest request) {
        GameObject object = gameObjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Object not found"));

        if (request.getName() != null) {
            object.setName(request.getName());
        }

        if (request.getPrice() != null) {
            object.setPrice(request.getPrice());
        }

        if (request.getGameId() != null) {
            Game newGame = gameRepository.findById(request.getGameId()).orElseThrow(() -> new RuntimeException("Game not found with id: " + request.getGameId()));
            object.setGame(newGame);
        }
        return toResponse(object);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @Transactional
    public void delete(Long id) {

        GameObject object = gameObjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Object not found"));

        gameObjectRepository.delete(object);
    }

    // -----------------------------
    // GET ALL OBJECTS
    // -----------------------------
    public List<GameObjectResponse> getAll() {
        List<GameObject> objects = gameObjectRepository.findAll();
        return toResponseList(objects);
    }

    public List<GameObjectResponse> getMyObjects() {
        Long sellerId = userService.getCurrentUser().getId();
        List<GameObject> objects = gameObjectRepository.findBySellerId(sellerId);
        return toResponseList(objects);
    }

    public GameObjectResponse getById(Long id) {
        GameObject object = gameObjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Object not found"));
        return toResponse(object);
    }

    // -----------------------------
    // MAPPER
    // -----------------------------
    private GameObjectResponse toResponse(GameObject object) {
        GameObjectResponse dto = mapper.map(object, GameObjectResponse.class);

        dto.setSellerId(object.getSeller().getId());
        dto.setSellerFirstName(object.getSeller().getFirstName());
        dto.setSellerLastName(object.getSeller().getLastName());

        dto.setGameId(object.getGame().getId());
        dto.setGameTitle(object.getGame().getTitle());

        dto.setCreatedAt(object.getCreatedAt().toString());

        return dto;
    }

    private List<GameObjectResponse> toResponseList(List<GameObject> list) {
        return list.stream()
                .map(this::toResponse)
                .toList();
    }
}
