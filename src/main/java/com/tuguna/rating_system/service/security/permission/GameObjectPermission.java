package com.tuguna.rating_system.service.security.permission;

import com.tuguna.rating_system.repository.GameObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("gameObjectPermission")
public class GameObjectPermission {

    @Autowired
    private GameObjectRepository repository;

    public boolean canEdit(Long objectId, Long userId) {
        return repository.findById(objectId)
                .map(o -> o.getSeller().getId().equals(userId))
                .orElse(false);
    }
}