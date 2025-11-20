package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.ApiResponse;
import com.tuguna.rating_system.dto.auth.AuthResponse;
import com.tuguna.rating_system.dto.auth.LoginRequest;
import com.tuguna.rating_system.dto.auth.RegisterRequest;
import com.tuguna.rating_system.service.impl.AuthService;
import com.tuguna.rating_system.service.verify.VerificationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    /**
     * Login and get JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", auth));
    }

    @GetMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmEmail(@RequestParam String code) {
        String message = verificationCodeService.confirmEmail(code);
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }
}