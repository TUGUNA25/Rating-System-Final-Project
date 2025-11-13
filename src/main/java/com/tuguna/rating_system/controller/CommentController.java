package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.comment.CommentCreateDTO;
import com.tuguna.rating_system.dto.comment.CommentResponseDTO;
import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.impl.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{sellerId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    //  ADD COMMENT
    @PostMapping
    public CommentResponseDTO addComment(
            @PathVariable Long sellerId,
            @RequestBody CommentCreateDTO dto
    ) {

        return commentService.addComment(dto, sellerId,null);
    }

    //  GET APPROVED COMMENTS RECEIVED BY SELLER
    @GetMapping
    public List<CommentResponseDTO> getCommentsForSeller(@PathVariable Long sellerId) {
        return commentService.getCommentsForSeller(sellerId);
    }

    //  GET APPROVED COMMENTS WRITTEN BY SELLER
    //  /users/{sellerId}/comments/written
    @GetMapping("/written")
    public List<CommentResponseDTO> getWrittenComments(@PathVariable Long sellerId) {
        return commentService.getCommentsWrittenBySeller(sellerId);
    }

    //  GET SPECIFIC APPROVED COMMENT
    //  /users/{sellerId}/comments/{commentId}
    @GetMapping("/{commentId}")
    public CommentResponseDTO getSpecificComment(
            @PathVariable Long sellerId,
            @PathVariable Long commentId
    ) {
        return commentService.getSpecificComment(sellerId, commentId);
    }

}