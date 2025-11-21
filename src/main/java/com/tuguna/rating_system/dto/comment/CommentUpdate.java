package com.tuguna.rating_system.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CommentUpdate {
    private String content;
    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private int rating;
}

