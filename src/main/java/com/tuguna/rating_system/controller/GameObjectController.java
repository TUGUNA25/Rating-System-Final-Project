package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.gameobject.GameObjectCreateRequest;
import com.tuguna.rating_system.dto.gameobject.GameObjectResponse;
import com.tuguna.rating_system.dto.gameobject.GameObjectUpdateRequest;
import com.tuguna.rating_system.service.impl.GameObjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/object")
@RequiredArgsConstructor
public class GameObjectController {

    private final GameObjectService gameObjectService;

    // ----------------------------------------------------
    // CREATE
    // POST /object
    // ----------------------------------------------------
    @PostMapping
    public ApiResponse<GameObjectResponse> create(@Valid @RequestBody GameObjectCreateRequest request) {
        GameObjectResponse response = gameObjectService.create(request);
        return ApiResponse.success("Object created successfully", response);
    }

    // ----------------------------------------------------
    // GET ALL OBJECTS (PUBLIC)
    // GET /object
    // ----------------------------------------------------
    @GetMapping
    public ApiResponse<List<GameObjectResponse>> search(@RequestParam(required = false) String gametitle, @RequestParam(required = false) Long sellerId) {
        return ApiResponse.success("Objects fetched successfully", gameObjectService.getall(gametitle, sellerId)
        );
    }

    // ----------------------------------------------------
    // GET MY OBJECTS (AUTH REQUIRED)
    // GET /object/my
    // ----------------------------------------------------
    @GetMapping("/my")
    public ApiResponse<List<GameObjectResponse>> getMyObjects() {
        return ApiResponse.success("Your objects fetched successfully", gameObjectService.getMyObjects());
    }

    // ----------------------------------------------------
    // GET BY ID (PUBLIC)
    // GET /object/{id}
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ApiResponse<GameObjectResponse> getById(@PathVariable Long id) {
        GameObjectResponse object = gameObjectService.getById(id);
        return ApiResponse.success("Object fetched successfully", object);
    }

    // ----------------------------------------------------
    // UPDATE OBJECT (ONLY OWNER CAN EDIT)
    // PUT /object/{id}
    // ----------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("@gameObjectPermission.canEdit(#id, authentication.principal.id)")
    public ApiResponse<GameObjectResponse> update(@PathVariable Long id, @Valid @RequestBody GameObjectUpdateRequest request) {
        GameObjectResponse updated = gameObjectService.update(id, request);
        return ApiResponse.success("Object updated successfully", updated);
    }

    // ----------------------------------------------------
    // DELETE OBJECT (ONLY OWNER CAN DELETE)
    // DELETE /object/{id}
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("@gameObjectPermission.canEdit(#id, authentication.principal.id)")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        gameObjectService.delete(id);
        return ApiResponse.success("Object deleted successfully", null);
    }
}