package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.auth.AuthResponse;
import com.tuguna.rating_system.dto.auth.LoginRequest;
import com.tuguna.rating_system.dto.auth.RegisterRequest;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.Role;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SELLER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token);
    }
}