package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.auth.AuthResponse;
import com.tuguna.rating_system.dto.auth.LoginRequest;
import com.tuguna.rating_system.dto.auth.RegisterRequest;
import com.tuguna.rating_system.exception.ApiException;
import com.tuguna.rating_system.exception.ErrorCode;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.model.enums.Role;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.security.JwtService;
import com.tuguna.rating_system.service.verify.EmailService;
import com.tuguna.rating_system.service.verify.VerificationCodeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService,VerificationCodeService verificationCodeService,EmailService emailService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
    }


    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS, "User with this email already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SELLER)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        String code = verificationCodeService.generateAndStoreCode(user.getId());

        String confirmationLink = "http://localhost:8080/auth/confirm?code=" + code;

        emailService.sendVerificationEmail(user.getEmail(), confirmationLink);

        return "Registration successful! Please check your email to confirm your account.";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
        }

        if (!user.getEmailVerified()) {
            throw new ApiException(ErrorCode.EMAIL_NOT_VERIFIED, "Please confirm your email before logging in.");
        }

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token);
    }

    public String resetPassword(String code, String newPassword) {
        Long userId = verificationCodeService.getUserIdByResetCode(code);
        if (userId == null) {
            throw new ApiException(ErrorCode.INVALID_CODE, "Invalid or expired reset code");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        verificationCodeService.deleteResetCode(code);
        return "Password has been successfully reset!";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with this email"));
        String code = verificationCodeService.generateResetCode(user.getId());
        emailService.sendPasswordResetEmail(email, code);
        return "Password reset code has been sent to your email.";
    }
}