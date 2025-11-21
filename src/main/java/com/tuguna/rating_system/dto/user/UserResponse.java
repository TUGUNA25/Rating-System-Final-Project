package com.tuguna.rating_system.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Double averageRating;
    private Integer ratingsCount;
}