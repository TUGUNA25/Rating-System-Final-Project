package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.gameobject.GameObjectRequest;
import com.tuguna.rating_system.dto.gameobject.GameObjectResponse;
import com.tuguna.rating_system.service.impl.GameObjectService;
import com.tuguna.rating_system.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/object")
@RequiredArgsConstructor
public class GameObjectController {

    private final GameObjectService gameObjectService;
    private final UserService userService;

    // ----------------------------------------------------
    // CREATE
    // POST /object
    // ----------------------------------------------------
    @PostMapping
    public GameObjectResponse create(@RequestBody GameObjectRequest request) {
        return gameObjectService.create(request);
    }

    // ----------------------------------------------------
    // GET ALL OBJECTS (PUBLIC)
    // GET /object
    // ----------------------------------------------------
    @GetMapping
    public List<GameObjectResponse> getAll() {
        return gameObjectService.getAll();
    }

    // ----------------------------------------------------
    // GET MY OBJECTS (AUTH REQUIRED)
    // GET /object/my
    // ----------------------------------------------------
    @GetMapping("/my")
    public List<GameObjectResponse> getMyObjects() {
        return gameObjectService.getMyObjects();
    }

    // ----------------------------------------------------
    // GET BY ID (PUBLIC)
    // GET /object/{id}
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public GameObjectResponse getById(@PathVariable Long id) {
        return gameObjectService.getById(id);
    }

    // ----------------------------------------------------
    // UPDATE OBJECT (ONLY OWNER CAN EDIT)
    // PUT /object/{id}
    // ----------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("@gameObjectPermission.canEdit(#id, authentication.principal.id)")
    public GameObjectResponse update(
            @PathVariable Long id,
            @RequestBody GameObjectRequest request
    ) {
        return gameObjectService.update(id, request);
    }

    // ----------------------------------------------------
    // DELETE OBJECT (ONLY OWNER CAN DELETE)
    // DELETE /object/{id}
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("@gameObjectPermission.canEdit(#id, authentication.principal.id)")
    public void delete(@PathVariable Long id) {
        gameObjectService.delete(id);
    }
}