package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.dto.game.GameRequest;
import com.tuguna.rating_system.dto.game.GameResponse;
import com.tuguna.rating_system.service.impl.CommentService;
import com.tuguna.rating_system.service.impl.GameService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getPendingComments() {
        List<CommentResponse> list = commentService.getPendingComments();
        return ResponseEntity.ok(ApiResponse.success("Pending comments retrieved", list));
    }

    /**
     * Approve a pending comment
     */
    @PutMapping("/comments/{commentId}/approve")
    public ResponseEntity<ApiResponse<CommentResponse>> approveComment(@PathVariable Long commentId) {
        CommentResponse response = commentService.approveComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment approved", response));
    }

    /**
     * Delete any comment
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> adminDeleteComment(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted by admin", null));
    }

    /**
     * Reject a pending comment (delete it)
     */
    public ResponseEntity<ApiResponse<String>> rejectComment(@PathVariable Long commentId) {
        commentService.rejectPendingComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment rejected and deleted", null));
    }

    @PostMapping("/games")
    public ApiResponse<GameResponse> create(@Valid @RequestBody GameRequest request) {
        GameResponse response = gameService.create(request.getTitle());
        return ApiResponse.success("Game created successfully", response);
    }

    @PutMapping("/games/{id}")
    public ApiResponse<GameResponse> update(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        GameResponse updated = gameService.update(id, request.getTitle());
        return ApiResponse.success("Game updated successfully", updated);
    }

    @DeleteMapping("/games/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ApiResponse.success("Game deleted successfully", null);
    }

    @GetMapping("/games")
    public ApiResponse<List<GameResponse>> getAll() {
        return ApiResponse.success("Games fetched successfully", gameService.getAll());
    }

    @GetMapping("/games/{id}")
    public ApiResponse<GameResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Game fetched successfully", gameService.getById(id));
    }

}