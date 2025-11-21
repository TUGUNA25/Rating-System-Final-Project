package com.tuguna.rating_system.repository;

import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    // Get all objects from specific seller
    List<GameObject> findBySeller(User seller);

    // Get all objects for specific game
    List<GameObject> findByGame(Game game);

    // Used to validate ownership: object must belong to seller
    Optional<GameObject> findByIdAndSeller(Long id, User seller);
    List<GameObject> findBySellerId(Long sellerId);
}