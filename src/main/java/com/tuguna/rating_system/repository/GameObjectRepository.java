package com.tuguna.rating_system.repository;

import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.entity.User.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findBySeller(User seller);

    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findByGame(Game game);

    @EntityGraph(attributePaths = {"seller", "game"})
    Optional<GameObject> findByIdAndSeller(Long id, User seller);

    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findBySellerId(Long sellerId);
    @Override
    @EntityGraph(attributePaths = {"seller", "game"})
    List<GameObject> findAll();

}