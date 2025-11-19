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


    // All objects/items that belong to this game
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<GameObject> objects;
}