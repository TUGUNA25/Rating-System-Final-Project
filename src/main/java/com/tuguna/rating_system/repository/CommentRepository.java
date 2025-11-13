package com.tuguna.rating_system.repository;

import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySellerAndStatus(User seller, CommentStatus status);
    List<Comment> findByAuthorAndStatus(User author,CommentStatus status);
    Optional<Comment> findByIdAndSellerAndStatus(Long commentId, User seller,CommentStatus status);
}
