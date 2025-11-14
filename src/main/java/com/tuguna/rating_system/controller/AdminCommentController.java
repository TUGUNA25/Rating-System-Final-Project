package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.comment.CommentResponseDTO;
import com.tuguna.rating_system.service.impl.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    /**
     * Get all pending comment
     */
    @GetMapping("/pending")
    public List<CommentResponseDTO> getPendingComments() {
        return commentService.getPendingComments();
    }

    /**
     * Approve a pending comment
     */
    @PutMapping("/{commentId}/approve")
    public CommentResponseDTO approveComment(@PathVariable Long commentId) {
        return commentService.approveComment(commentId);
    }

    /**
     * Reject a pending comment (delete it)
     */
    @DeleteMapping("/{commentId}/reject")
    public ResponseEntity<String> rejectComment(@PathVariable Long commentId) {
        commentService.rejectPendingComment(commentId);
        return ResponseEntity.ok("Comment rejected and deleted");
    }
}