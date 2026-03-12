package com.aniva.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.auth.dto.AuthResponse;
import com.aniva.modules.auth.dto.LoginRequest;
import com.aniva.modules.auth.dto.RegisterRequest;
import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.auth.service.AuthService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🔐 REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword()
        );

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Registration successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(
                request.getIdentifier(),
                request.getPassword()
        );

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔐 CURRENT USER PROFILE
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me() {

        UserProfileResponse profile = authService.getCurrentUser();

        ApiResponse<UserProfileResponse> response =
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .data(profile)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
        @RequestParam String refreshToken) {

        AuthResponse authResponse =
                authService.refreshToken(refreshToken);

        ApiResponse<AuthResponse> response =
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

    return ResponseEntity.ok(response);
    }
}