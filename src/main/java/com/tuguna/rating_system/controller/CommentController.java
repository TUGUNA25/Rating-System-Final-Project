package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.comment.CommentCreate;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.dto.comment.CommentUpdate;
import com.tuguna.rating_system.service.impl.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{sellerId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    //  ADD COMMENT
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable Long sellerId,@Valid @RequestBody CommentCreate dto) {
        CommentResponse response = commentService.addComment(dto, sellerId);
        return ResponseEntity.ok(ApiResponse.success("Comment submitted (pending approval)", response));
    }

    //  GET APPROVED COMMENTS RECEIVED BY SELLER
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsForSeller(@PathVariable Long sellerId) {
        List<CommentResponse> list = commentService.getCommentsForSeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success("Approved comments retrieved", list));
    }

    //  GET APPROVED COMMENTS WRITTEN BY SELLER
    //  /users/{sellerId}/comments/my
    @GetMapping("/my")
    @PreAuthorize("#sellerId == authentication.principal.id and hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getMyWrittenComments() {

        List<CommentResponse> list = commentService.getCommentsWrittenBySeller();
        return ResponseEntity.ok(ApiResponse.success("Your written comments", list));
    }

    //  GET SPECIFIC APPROVED COMMENT
    //  /users/{sellerId}/comments/{commentId}
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getSpecificComment(@PathVariable Long sellerId, @PathVariable Long commentId) {
        CommentResponse response = commentService.getSpecificComment(sellerId, commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment retrieved", response));
    }

    //  Update SPECIFIC COMMENT
    //  /users/{sellerId}/comments/{commentId}
    @PutMapping("/{commentId}")
    @PreAuthorize("@commentPermission.canEdit(#commentId, authentication.principal.id)")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentUpdate dto) {
        CommentResponse updated = commentService.updateComment(commentId, dto);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", updated));
    }

    // delete comment
    // /users/{sellerId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    @PreAuthorize("@commentPermission.canEdit(#commentId, authentication.principal.id)")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }

}