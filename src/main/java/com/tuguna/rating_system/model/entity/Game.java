package com.tuguna.rating_system.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String genre;
    private Double price;
    private String platform; // e.g., PC, Xbox, PlayStation

    @ManyToMany(mappedBy = "games")
    private Set<User> sellers;
}