package com.tuguna.rating_system.dto.comment;

import lombok.Data;

@Data
public class CommentCreate {
    private String content;
    private int rating;
}
