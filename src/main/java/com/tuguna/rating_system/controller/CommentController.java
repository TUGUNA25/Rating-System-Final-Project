package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.comment.CommentCreateDTO;
import com.tuguna.rating_system.dto.comment.CommentResponseDTO;
import com.tuguna.rating_system.service.impl.CommentService;
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
    public CommentResponseDTO addComment(@PathVariable Long sellerId, @RequestBody CommentCreateDTO dto) {
        return commentService.addComment(dto, sellerId);
    }

    //  GET APPROVED COMMENTS RECEIVED BY SELLER
    @GetMapping
    public List<CommentResponseDTO> getCommentsForSeller(@PathVariable Long sellerId) {
        return commentService.getCommentsForSeller(sellerId);
    }

    //  GET APPROVED COMMENTS WRITTEN BY SELLER
    //  /users/{sellerId}/comments/written
    @GetMapping("/written")
    @PreAuthorize("#sellerId == authentication.principal.id and hasRole('SELLER')")
    public List<CommentResponseDTO> getWrittenComments(@PathVariable Long sellerId) {
        return commentService.getCommentsWrittenBySeller(sellerId);
    }

    //  GET SPECIFIC APPROVED COMMENT
    //  /users/{sellerId}/comments/{commentId}
    @GetMapping("/{commentId}")
    public CommentResponseDTO getSpecificComment(@PathVariable Long sellerId, @PathVariable Long commentId) {
        return commentService.getSpecificComment(sellerId, commentId);
    }
    // delete comment
    // /users/{sellerId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

}