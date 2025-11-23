package com.tuguna.rating_system.dto.comment;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private int rating;
    private String status;
    private Long sellerId;
    private Long authorId;
    private LocalDateTime createdAt;
}