package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.comment.CommentCreateDTO;
import com.tuguna.rating_system.dto.comment.CommentResponseDTO;
import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import com.tuguna.rating_system.repository.CommentRepository;
import com.tuguna.rating_system.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public CommentResponseDTO addComment(CommentCreateDTO dto, Long sellerId, Long authorId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        User author = null;
        if (authorId != null) {
            author = userRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .seller(seller)
                .author(author)
                .status(CommentStatus.PENDING)
                .build();

        Comment saved = commentRepository.save(comment);

        // 🔥 Map entity → DTO
        CommentResponseDTO response = mapper.map(saved, CommentResponseDTO.class);

        // Fixing nested objects manually
        response.setSellerId(saved.getSeller().getId());
        response.setAuthorId(saved.getAuthor() != null ? saved.getAuthor().getId() : null);

        return response;
    }

    //comments that seller get from people
    public List<CommentResponseDTO> getCommentsForSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        List<Comment> comments = commentRepository.findBySellerAndStatus(seller, CommentStatus.APPROVED);

        return comments.stream()
                .map(comment -> {
                    CommentResponseDTO dto = mapper.map(comment, CommentResponseDTO.class);
                    dto.setSellerId(comment.getSeller().getId());
                    dto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
                    return dto;
                })
                .toList();
    }

    //comments that seller writes for other sellers profile
    public List<CommentResponseDTO> getCommentsWrittenBySeller(Long sellerId) {
        User author = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + sellerId));

        List<Comment> comments = commentRepository.findByAuthorAndStatus(author, CommentStatus.APPROVED);

        return comments.stream()
                .map(comment -> {
                    CommentResponseDTO dto = mapper.map(comment, CommentResponseDTO.class);
                    dto.setSellerId(comment.getSeller().getId());
                    dto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
                    return dto;
                })
                .toList();
    }

    // get a specific comment
    public CommentResponseDTO getSpecificComment(Long sellerId, Long commentId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null) {
            return null; // temporary, later we do 404
        }

        // 2. Find comment belonging to seller AND approved
        Comment comment = commentRepository.findByIdAndSellerAndStatus(
                commentId,
                seller,
                CommentStatus.APPROVED
        ).orElse(null);

        if (comment == null) {
            return null; // temporary, later we do 404
        }

        CommentResponseDTO dto = mapper.map(comment, CommentResponseDTO.class);

        dto.setSellerId(comment.getSeller().getId());
        dto.setAuthorId(
                comment.getAuthor() != null ? comment.getAuthor().getId() : null
        );

        return dto;
    }


    public void deleteComment(Long commentId, Long authorId) {
        // Will add logic
    }

}
