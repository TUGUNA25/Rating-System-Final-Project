package com.tuguna.rating_system.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.comment.CommentCreate;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import com.tuguna.rating_system.model.enums.Role;
import com.tuguna.rating_system.repository.CommentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User seller;

    @BeforeEach
    void setUp() {
        // Clean up
        commentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test seller
        seller = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .password("password123")
                .role(Role.SELLER)
                .emailVerified(true)
                .build();
        seller = userRepository.save(seller);
    }

    @Test
    void addComment_WithoutAuth_AnonymousUser_CreatesPendingComment() throws Exception {
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("Great seller, very responsive!");
        commentCreate.setRating(5);

        String requestBody = objectMapper.writeValueAsString(commentCreate);

        MvcResult result = mockMvc.perform(post("/users/{sellerId}/comments", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<CommentResponse> apiResponse = objectMapper.readValue(
                responseBody, 
                new TypeReference<ApiResponse<CommentResponse>>() {}
        );
        
        assertTrue(apiResponse.isSuccess());
        assertEquals("Comment submitted (pending approval)", apiResponse.getMessage());

        CommentResponse commentResponse = apiResponse.getData();
        
        assertNotNull(commentResponse);
        assertEquals("Great seller, very responsive!", commentResponse.getContent());
        assertEquals(5, commentResponse.getRating());
        assertEquals(seller.getId(), commentResponse.getSellerId());
        assertNull(commentResponse.getAuthorId()); // Anonymous user, so authorId should be null

        var savedComment = commentRepository.findById(commentResponse.getId());
        assertTrue(savedComment.isPresent());
        assertEquals(CommentStatus.PENDING, savedComment.get().getStatus());
        assertEquals(seller.getId(), savedComment.get().getSeller().getId());
        assertNull(savedComment.get().getAuthor()); // Anonymous user
    }

    @Test
    void addComment_WithInvalidRating_ReturnsBadRequest() throws Exception {
        CommentCreate commentCreate = new CommentCreate();
        commentCreate.setContent("Test comment");
        commentCreate.setRating(6); // Invalid rating (max is 5)

        String requestBody = objectMapper.writeValueAsString(commentCreate);

        mockMvc.perform(post("/users/{sellerId}/comments", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}

