package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.dto.game.GameRequest;
import com.tuguna.rating_system.dto.game.GameResponse;
import com.tuguna.rating_system.dto.user.ChangeRoleRequest;
import com.tuguna.rating_system.dto.user.UserResponse;
import com.tuguna.rating_system.service.impl.CommentService;
import com.tuguna.rating_system.service.impl.GameService;
import com.tuguna.rating_system.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CommentService commentService;
    private final GameService gameService;
    private final UserService userService;

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

    // Get ALL users (admins included)
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> list = userService.adminGetAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", list));
    }

    // Get ANY user
    @GetMapping("users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse response = userService.adminGetUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", response));
    }

    // Verify manually
    @PutMapping("users/{id}/verify")
    public ResponseEntity<ApiResponse<UserResponse>> verify(@PathVariable Long id) {
        UserResponse updated = userService.adminVerifyUser(id);
        return ResponseEntity.ok(ApiResponse.success("User verified", updated));
    }

    // Change role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request) {
        UserResponse updated = userService.adminChangeRole(id, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", updated));
    }

    // Delete any user
    @DeleteMapping("users/{id}")
    public ResponseEntity<ApiResponse<String>> userdelete(@PathVariable Long id) {
        userService.adminDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

}