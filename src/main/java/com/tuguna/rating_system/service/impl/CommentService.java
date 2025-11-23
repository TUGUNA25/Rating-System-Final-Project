package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.comment.CommentCreate;
import com.tuguna.rating_system.dto.comment.CommentResponse;
import com.tuguna.rating_system.dto.comment.CommentUpdate;
import com.tuguna.rating_system.exception.ApiException;
import com.tuguna.rating_system.exception.ErrorCode;
import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User.User;
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

        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Seller not found with id: " + sellerId));

        // current authenticated user (null if user is anonymous)
        CustomUserDetails currentUser = userService.getCurrentUser();

        User author = null;
        if (currentUser != null) {
            author = userRepository.getReferenceById(currentUser.getId());
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
        if (!userRepository.existsById(sellerId)) {throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Seller not found with id: " + sellerId);
        }
        User sellerRef = userRepository.getReferenceById(sellerId);
        List<Comment> comments = commentRepository.findBySellerAndStatus(sellerRef, CommentStatus.APPROVED);
        return mapToResponseList(comments);
    }


    //comments that seller writes for other sellers profile
    public List<CommentResponse> getCommentsWrittenBySeller() {
        CustomUserDetails currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        User author = userRepository.getReferenceById(userId);
        List<Comment> comments = commentRepository.findByAuthorAndStatus(author, CommentStatus.APPROVED);
        return mapToResponseList(comments);
    }

    // get a specific comment
    public CommentResponse getSpecificComment(Long sellerId, Long commentId) {
        if (!userRepository.existsById(sellerId)) {throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Seller not found with id: " + sellerId);
        }
        User sellerRef = userRepository.getReferenceById(sellerId);
        Comment comment = commentRepository.findByIdAndSellerAndStatus(commentId, sellerRef, CommentStatus.APPROVED).orElseThrow(() -> new ApiException(
                ErrorCode.RESOURCE_NOT_FOUND,
                "Approved comment not found"
        ));

        return mapToResponseDTO(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdate dto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Comment not found"
                ));

        if (dto.getContent() != null) {
            comment.setContent(dto.getContent());
        }

        if (dto.getRating() != null) {
            comment.setRating(dto.getRating());
        }

        // After editing comment must be reapproved by admin
        comment.setStatus(CommentStatus.PENDING);

        userService.updateSellerRating(comment.getSeller());

        return mapToResponseDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Comment not found"));
        User seller = comment.getSeller();
        commentRepository.delete(comment);
        userService.updateSellerRating(seller);
    }

    @Transactional
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Comment not found"));
        User seller = comment.getSeller();
        commentRepository.delete(comment);
        userService.updateSellerRating(seller);
    }
    public List<CommentResponse> getPendingComments() {
        List<Comment> pending = commentRepository.findByStatus(CommentStatus.PENDING);
        return mapToResponseList(pending);
    }

    @Transactional
    public CommentResponse approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Comment not found with id: " + commentId));
        comment.setStatus(CommentStatus.APPROVED);
        userService.updateSellerRating(comment.getSeller());
        return mapToResponseDTO(comment);
    }

    @Transactional
    public void rejectPendingComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Comment not found with id: " + commentId));
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Only pending comments can be rejected");
        }
        commentRepository.delete(comment);
    }


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
