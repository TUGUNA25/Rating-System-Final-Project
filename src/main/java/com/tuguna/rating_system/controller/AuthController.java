package com.tuguna.rating_system.controller;

import com.tuguna.rating_system.dto.auth.AuthResponse;
import com.tuguna.rating_system.dto.auth.LoginRequest;
import com.tuguna.rating_system.dto.auth.RegisterRequest;
import com.tuguna.rating_system.service.impl.AuthService;
import com.tuguna.rating_system.service.verify.VerificationCodeService;
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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Login and get JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String code) {
        return ResponseEntity.ok(verificationCodeService.confirmEmail(code));
    }
}