package com.tuguna.rating_system.service.security.permission;

import com.tuguna.rating_system.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("commentPermission")
@RequiredArgsConstructor
public class CommentPermission {
    private final CommentRepository commentRepository;
    public boolean canEdit(Long commentId, Long userId) {
        return commentRepository.findById(commentId)
                .map(c -> c.getAuthor() != null && c.getAuthor().getId().equals(userId))
                .orElse(false);
    }
}