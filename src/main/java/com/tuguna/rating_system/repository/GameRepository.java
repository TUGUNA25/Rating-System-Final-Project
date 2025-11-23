package com.tuguna.rating_system.repository;

import com.tuguna.rating_system.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByTitleIgnoreCase(String title);
}