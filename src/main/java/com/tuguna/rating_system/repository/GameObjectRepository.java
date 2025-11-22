package com.tuguna.rating_system.repository;


import com.tuguna.rating_system.model.entity.GameObject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findBySellerId(Long sellerId);
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findByGame_TitleIgnoreCaseContaining(String title);
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findBySellerIdAndGame_TitleIgnoreCaseContaining(Long sellerId, String title);
    @Override
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findAll();
}