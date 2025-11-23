package com.tuguna.rating_system;

import com.tuguna.rating_system.dto.comment.CommentCreate;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import com.tuguna.rating_system.repository.CommentRepository;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.impl.CommentService;
import com.tuguna.rating_system.service.impl.UserService;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(
                commentRepository,
                userRepository,
                new ModelMapper(),
                userService
        );
    }

    @Test
    void addComment_anonymousUser_createsPendingCommentForSeller() {
        Long sellerId = 1L;
        CommentCreate dto = new CommentCreate();
        dto.setContent("Great seller");
        dto.setRating(5);

        User seller = new User();
        seller.setId(sellerId);

        Mockito.when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        Mockito.when(userService.getCurrentUser()).thenReturn(null);

        Mockito.when(commentRepository.save(any(Comment.class)))
                .thenAnswer(inv -> {
                    Comment c = inv.getArgument(0);
                    c.setId(10L);
                    return c;
                });

        CommentResponse response = commentService.addComment(dto, sellerId);
        assertNotNull(response);
        assertEquals(sellerId, response.getSellerId());
        assertNull(response.getAuthorId());
        Mockito.verify(commentRepository).save(Mockito.argThat(c ->
                c.getStatus() == CommentStatus.PENDING &&
                        c.getSeller().getId().equals(sellerId)
        ));
    }
}