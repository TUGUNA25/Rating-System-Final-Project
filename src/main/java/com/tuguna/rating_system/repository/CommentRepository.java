package com.tuguna.rating_system.repository;

import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySellerAndStatus(User seller, CommentStatus status);
    List<Comment> findByAuthorAndStatus(User author,CommentStatus status);
    Optional<Comment> findByIdAndSellerAndStatus(Long commentId, User seller,CommentStatus status);
    List<Comment> findByStatus(CommentStatus status);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.seller.id = :sellerId AND c.status = 'APPROVED'")
    long countApproved(@Param("sellerId") Long sellerId);
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.seller.id = :sellerId AND c.status = 'APPROVED'")
    Double averageApproved(@Param("sellerId") Long sellerId);
}
