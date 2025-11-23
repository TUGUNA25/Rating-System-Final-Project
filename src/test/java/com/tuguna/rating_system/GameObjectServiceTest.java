package com.tuguna.rating_system;

import com.tuguna.rating_system.dto.gameobject.GameObjectCreateRequest;
import com.tuguna.rating_system.dto.gameobject.GameObjectResponse;
import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.repository.GameObjectRepository;
import com.tuguna.rating_system.repository.GameRepository;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.impl.GameObjectService;
import com.tuguna.rating_system.service.impl.UserService;
import com.tuguna.rating_system.service.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GameObjectServiceTest {

    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private GameObjectService gameObjectService;

    @BeforeEach
    void setUp() {
        gameObjectService = new GameObjectService(
                gameObjectRepository,
                gameRepository,
                userRepository,
                userService,
                new ModelMapper()
        );
    }

    @Test
    void create_shouldCreateObjectForCurrentSellerAndGame() {

        Long sellerId = 5L;
        Long gameId = 3L;

        GameObjectCreateRequest request = new GameObjectCreateRequest();
        request.setName("AK-47 Skin");
        request.setPrice(19.99);
        request.setGameId(gameId);

        CustomUserDetails currentUser = Mockito.mock(CustomUserDetails.class);
        Mockito.when(currentUser.getId()).thenReturn(sellerId);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        User seller = new User();
        seller.setId(sellerId);

        Game game = new Game();
        game.setId(gameId);
        game.setTitle("CS2");

        Mockito.when(userRepository.findById(sellerId))
                .thenReturn(Optional.of(seller));
        Mockito.when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(game));

        Mockito.when(gameObjectRepository.save(any(GameObject.class)))
                .thenAnswer(inv -> {
                    GameObject obj = inv.getArgument(0);
                    obj.setId(100L);
                    return obj;
                });

        GameObjectResponse response = gameObjectService.create(request);

        assertNotNull(response);
        assertEquals("AK-47 Skin", response.getName());
        assertEquals(19.99, response.getPrice());
        assertEquals(sellerId, response.getSellerId());
        assertEquals(gameId, response.getGameId());
    }
}