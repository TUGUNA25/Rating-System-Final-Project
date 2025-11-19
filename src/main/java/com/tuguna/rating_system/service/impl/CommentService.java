package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.comment.CommentCreate;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import com.tuguna.rating_system.repository.CommentRepository;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,ModelMapper mapper,UserService userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public CommentResponse addComment(CommentCreate dto, Long sellerId) {

        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        // user from JWT
        CustomUserDetails currentUser = userService.getCurrentUser();

        User author = null;
        if (currentUser != null) {
            Long userId = currentUser.getId();
            author = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .seller(seller)
                .author(author)   // null if not logged in
                .status(CommentStatus.PENDING)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToResponseDTO(saved);
    }

    //comments that seller get from people
    public List<CommentResponse> getCommentsForSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        List<Comment> comments = commentRepository.findBySellerAndStatus(seller, CommentStatus.APPROVED);

        return mapToResponseList(comments);
    }

    //comments that seller writes for other sellers profile
    // gadasaweria !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public List<CommentResponse> getCommentsWrittenBySeller(Long sellerId) {
        User author = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found with id: " + sellerId));

        List<Comment> comments = commentRepository.findByAuthorAndStatus(author, CommentStatus.APPROVED);

        return mapToResponseList(comments);
    }

    // get a specific comment
    public CommentResponse getSpecificComment(Long sellerId, Long commentId) {

        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));
        Comment comment = commentRepository.findByIdAndSellerAndStatus(
                        commentId,
                        seller,
                        CommentStatus.APPROVED
                ).orElseThrow(() -> new RuntimeException("Approved comment not found"));

        return mapToResponseDTO(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentCreate dto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Update fields
        if (dto.getContent() != null) {
            comment.setContent(dto.getContent());
        }

        if (dto.getRating() != 0) {
            comment.setRating(dto.getRating());
            userService.updateSellerRating(comment.getSeller());
        }

        return mapToResponseDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        CustomUserDetails userDetails = userService.getCurrentUser();

        if (userDetails == null) {
            throw new RuntimeException("You must be logged in to delete comments");
        }

        Long userId = userDetails.getId();

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }
        User seller = comment.getSeller();
        commentRepository.delete(comment);
        userService.updateSellerRating(seller);
    }

    @Transactional
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User seller = comment.getSeller(); // store before delete
        commentRepository.delete(comment);
        userService.updateSellerRating(seller);
    }
    public List<CommentResponse> getPendingComments() {
        List<Comment> pending = commentRepository.findByStatus(CommentStatus.PENDING);
        return mapToResponseList(pending);
    }

    @Transactional
    public CommentResponse approveComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setStatus(CommentStatus.APPROVED);

        userService.updateSellerRating(comment.getSeller());

        return mapToResponseDTO(comment);
    }

    @Transactional
    public void rejectPendingComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new RuntimeException("Only pending comments can be rejected");
        }

        commentRepository.delete(comment);
    }



    // mapping helpers
    private CommentResponse mapToResponseDTO(Comment comment) {
        CommentResponse dto = mapper.map(comment, CommentResponse.class);
        dto.setSellerId(comment.getSeller().getId());
        dto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
        return dto;
    }

    private List<CommentResponse> mapToResponseList(List<Comment> comments) {
        return comments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }


}
