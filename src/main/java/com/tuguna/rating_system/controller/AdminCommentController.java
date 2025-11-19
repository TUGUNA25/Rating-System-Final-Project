package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.dto.game.GameRequest;
import com.tuguna.rating_system.dto.game.GameResponse;
import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.service.impl.CommentService;
import com.tuguna.rating_system.service.impl.GameService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;
    private final GameService gameService;

    /**
     * Get all pending comment
     */
    @GetMapping("/comments/pending")
    public List<CommentResponse> getPendingComments() {
        return commentService.getPendingComments();
    }

    /**
     * Approve a pending comment
     */
    @PutMapping("/comments/{commentId}/approve")
    public CommentResponse approveComment(@PathVariable Long commentId) {
        return commentService.approveComment(commentId);
    }

    /**
     * Delete any comment
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> adminDeleteComment(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
        return ResponseEntity.ok("Comment deleted by admin");
    }

    /**
     * Reject a pending comment (delete it)
     */
    @DeleteMapping("/comments/{commentId}/reject")
    public ResponseEntity<String> rejectComment(@PathVariable Long commentId) {
        commentService.rejectPendingComment(commentId);
        return ResponseEntity.ok("Comment rejected and deleted");
    }

    @PostMapping("/games")
    public GameResponse createGame(@RequestBody GameRequest request) {
        return gameService.create(request.getTitle());
    }

    @PutMapping("/games/{id}")
    public GameResponse updateGame(@PathVariable Long id, @RequestBody GameRequest request) {
        return gameService.update(id, request.getTitle());
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.ok("Game deleted");
    }

    @GetMapping("/games")
    public List<GameResponse> getAllGames() {
        return gameService.getAll();
    }

    @GetMapping("/games/{id}")
    public GameResponse getGameById(@PathVariable Long id) {
        return gameService.getById(id);
    }

}