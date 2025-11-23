package com.tuguna.rating_system.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.gameobject.GameObjectResponse;
import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.model.enums.Role;
import com.tuguna.rating_system.repository.GameObjectRepository;
import com.tuguna.rating_system.repository.GameRepository;
import com.tuguna.rating_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GameObjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User seller;
    private Game game;
    private GameObject gameObject1;
    private GameObject gameObject2;

    @BeforeEach
    void setUp() {
        // Clean up
        gameObjectRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();

        // Create test seller
        seller = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .role(Role.SELLER)
                .emailVerified(true)
                .build();
        seller = userRepository.save(seller);

        // Create test game
        game = Game.builder()
                .title("Counter-Strike 2")
                .build();
        game = gameRepository.save(game);

        // Create test game objects
        gameObject1 = GameObject.builder()
                .name("AK-47 Skin")
                .price(19.99)
                .seller(seller)
                .game(game)
                .build();
        gameObject1 = gameObjectRepository.save(gameObject1);

        gameObject2 = GameObject.builder()
                .name("AWP Skin")
                .price(29.99)
                .seller(seller)
                .game(game)
                .build();
        gameObject2 = gameObjectRepository.save(gameObject2);
    }

    @Test
    void getAllGameObjects_WithoutAuth_ReturnsAllObjects() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/object")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify response
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<List<GameObjectResponse>> apiResponse = objectMapper.readValue(
                responseBody, 
                new TypeReference<ApiResponse<List<GameObjectResponse>>>() {}
        );
        
        assertTrue(apiResponse.isSuccess());
        assertEquals("Objects fetched successfully", apiResponse.getMessage());
        
        // Parse the data list
        List<GameObjectResponse> objects = apiResponse.getData();
        
        assertNotNull(objects);
        assertEquals(2, objects.size());
        
        // Verify first object
        GameObjectResponse obj1 = objects.stream()
                .filter(o -> o.getName().equals("AK-47 Skin"))
                .findFirst()
                .orElse(null);
        assertNotNull(obj1);
        assertEquals(19.99, obj1.getPrice());
        assertEquals(seller.getId(), obj1.getSellerId());
        assertEquals(game.getId(), obj1.getGameId());
    }

    @Test
    void getAllGameObjects_WithGameTitleFilter_ReturnsFilteredObjects() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/object")
                        .param("gametitle", "Counter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<List<GameObjectResponse>> apiResponse = objectMapper.readValue(
                responseBody, 
                new TypeReference<ApiResponse<List<GameObjectResponse>>>() {}
        );
        
        List<GameObjectResponse> objects = apiResponse.getData();
        
        assertNotNull(objects);
        assertEquals(2, objects.size());
        assertTrue(objects.stream().allMatch(o -> o.getGameTitle().contains("Counter")));
    }
}

