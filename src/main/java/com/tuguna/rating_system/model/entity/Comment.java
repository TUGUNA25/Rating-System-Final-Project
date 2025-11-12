package com.tuguna.rating_system.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // The seller who receives this comment
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // The user (seller or anonymous) who wrote it
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}