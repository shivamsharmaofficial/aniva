package com.aniva.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aniva.core.config.JwtUtil;
import com.aniva.modules.auth.dto.AuthResponse;
import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.auth.entity.*;
import com.aniva.modules.auth.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository providerRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    // =========================
    // REGISTER
    // =========================
    @Override
    public AuthResponse register(String firstName,
                                 String lastName,
                                 String email,
                                 String phoneNumber,
                                 String password) {

        validateRegisterInput(firstName, email, password);
        checkExistingUser(email, phoneNumber);

        User user = createUser(firstName, lastName, email, phoneNumber);
        User savedUser = userRepository.save(user);

        assignDefaultCustomerRole(savedUser);
        createLocalAuthProvider(savedUser, password);

        return generateAuthResponse(savedUser);
    }

    // =========================
    // LOGIN
    // =========================
    @Override
    public AuthResponse login(String identifier, String password) {

        User user = findUserByEmailOrPhone(identifier);
        validatePassword(user, password);

        updateLastLogin(user);

        return generateAuthResponse(user);
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    @Override
    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken tokenEntity = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (Boolean.TRUE.equals(tokenEntity.getRevoked()) ||
                tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        User user = tokenEntity.getUser();

        refreshTokenRepository.deleteByUser_Id(user.getId());

        return generateAuthResponse(user);
    }

    // =========================
    // CURRENT USER
    // =========================
    @Override
    public UserProfileResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .roles(roles)
                .build();
    }

    // =========================
    // HELPERS
    // =========================

    private AuthResponse generateAuthResponse(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

        String accessToken =
                jwtUtil.generateToken(user.getEmail(), roles);

        String refreshToken = UUID.randomUUID().toString();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now()
                        .plusDays(REFRESH_TOKEN_VALIDITY_DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    private void validateRegisterInput(String firstName,
                                       String email,
                                       String password) {

        if (firstName == null || firstName.isBlank())
            throw new RuntimeException("First name is required");

        if (email == null || email.isBlank())
            throw new RuntimeException("Email is required");

        if (password == null || password.isBlank())
            throw new RuntimeException("Password is required");
    }

    private void checkExistingUser(String email,
                                   String phoneNumber) {

        userRepository.findByEmail(email.trim().toLowerCase())
                .ifPresent(u -> {
                    throw new RuntimeException("User already exists with this email");
                });

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            userRepository.findByPhoneNumber(phoneNumber.trim())
                    .ifPresent(u -> {
                        throw new RuntimeException("Phone number already registered");
                    });
        }
    }
        
    private User createUser(String firstName,
                                String lastName,
                                String email,
                                String phoneNumber) {

        return User.builder()
                .uuid(UUID.randomUUID())
                .firstName(firstName.trim())
                .lastName(lastName != null ? lastName.trim() : null)
                .email(email.trim().toLowerCase())
                .phoneNumber(phoneNumber != null ? phoneNumber.trim() : null)
                .status("ACTIVE")
                .build();
    }

    private void assignDefaultCustomerRole(User user) {

        Role customerRole = roleRepository
                .findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() ->
                        new RuntimeException("Default role not found"));

        user.getRoles().add(customerRole);
        userRepository.save(user);
    }

    private void createLocalAuthProvider(User user, String password) {

        UserAuthProvider provider = UserAuthProvider.builder()
                .user(user)
                .provider("LOCAL")
                .passwordHash(passwordEncoder.encode(password))
                .build();

        providerRepository.save(provider);
    }

    private User findUserByEmailOrPhone(String identifier) {

        identifier = identifier.trim();

        Optional<User> userOptional;

        if (identifier.contains("@")) {
            userOptional =
                    userRepository.findByEmailWithRoles(identifier.toLowerCase());
        } else {
            userOptional =
                    userRepository.findByPhoneWithRoles(identifier);
        }

        return userOptional
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials"));
    }

    private void validatePassword(User user,
                                  String rawPassword) {

        UserAuthProvider provider =
                providerRepository
                        .findByUser_IdAndProvider(user.getId(), "LOCAL")
                        .orElseThrow(() ->
                                new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword,
                provider.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    private void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
}