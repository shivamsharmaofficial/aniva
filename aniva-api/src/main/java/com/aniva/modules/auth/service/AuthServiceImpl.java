package com.aniva.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aniva.core.config.JwtUtil;
import com.aniva.modules.auth.dto.AuthResponse;
import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.auth.entity.*;
import com.aniva.modules.auth.repository.*;
import com.aniva.modules.analytics.service.UserEventService; // 🔥 NEW

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository providerRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final UserEventService userEventService; // 🔥 NEW

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

        AuthResponse response = generateAuthResponse(savedUser, "REGISTER", "SYSTEM");

        // 🔥 TRACK EVENT
        userEventService.trackEvent(
                savedUser.getId(),
                "REGISTER_SUCCESS",
                "AUTH",
                savedUser.getEmail(),
                "REGISTER",
                "SYSTEM"
        );

        return response;
    }

    // =========================
    // LOGIN
    // =========================
    @Override
    public AuthResponse login(String identifier,
                             String password,
                             String deviceInfo,
                             String ipAddress) {

        User user = findUserByEmailOrPhone(identifier);
        validatePassword(user, password);

        updateLastLogin(user);

        AuthResponse response = generateAuthResponse(user, deviceInfo, ipAddress);

        // 🔥 TRACK EVENT
        userEventService.trackEvent(
                user.getId(),
                "LOGIN_SUCCESS",
                "AUTH",
                user.getEmail(),
                deviceInfo,
                ipAddress
        );

        return response;
    }

    // =========================
    // LOGOUT
    // =========================
    @Override
    public void logout(String refreshToken) {

        Long userId = jwtUtil.extractUserIdFromRefreshToken(refreshToken);
        String tokenId = jwtUtil.extractTokenId(refreshToken);

        RefreshToken tokenEntity =
                refreshTokenRepository
                        .findByUser_IdAndTokenIdAndRevokedFalse(userId, tokenId)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        // 🔥 TRACK EVENT
        userEventService.trackEvent(
                userId,
                "LOGOUT",
                "AUTH",
                String.valueOf(userId),
                tokenEntity.getDeviceInfo(),
                tokenEntity.getIpAddress()
        );
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    @Override
    public AuthResponse refreshToken(String refreshToken) {

        Long userId = jwtUtil.extractUserIdFromRefreshToken(refreshToken);
        String tokenId = jwtUtil.extractTokenId(refreshToken);

        RefreshToken tokenEntity =
                refreshTokenRepository
                        .findByUser_IdAndTokenIdAndRevokedFalse(userId, tokenId)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (Boolean.TRUE.equals(tokenEntity.getRevoked()) ||
                tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        if (!passwordEncoder.matches(refreshToken, tokenEntity.getToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        User user = tokenEntity.getUser();

        AuthResponse response = generateAuthResponse(
                user,
                tokenEntity.getDeviceInfo(),
                tokenEntity.getIpAddress()
        );

        // 🔥 TRACK EVENT
        userEventService.trackEvent(
                user.getId(),
                "TOKEN_REFRESH",
                "AUTH",
                user.getEmail(),
                tokenEntity.getDeviceInfo(),
                tokenEntity.getIpAddress()
        );

        return response;
    }

    // =========================
    // CURRENT USER
    // =========================
    @Override
    public UserProfileResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = getUserByEmail(email);

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
    // REDIS CACHE
    // =========================
    @Cacheable(value = "user-profile", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // HELPERS
    // =========================
    private AuthResponse generateAuthResponse(User user,
                                              String deviceInfo,
                                              String ipAddress) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

        String accessToken =
                jwtUtil.generateToken(user.getEmail(), roles);

        String tokenId = UUID.randomUUID().toString();

        String rawRefreshToken =
                jwtUtil.generateRefreshToken(user.getId(), tokenId);

        String hashedToken = passwordEncoder.encode(rawRefreshToken);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenId(tokenId)
                .token(hashedToken)
                .user(user)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    // ===== existing helper methods unchanged =====

    private void validateRegisterInput(String firstName, String email, String password) {
        if (firstName == null || firstName.isBlank())
            throw new RuntimeException("First name is required");

        if (email == null || email.isBlank())
            throw new RuntimeException("Email is required");

        if (password == null || password.isBlank())
            throw new RuntimeException("Password is required");
    }

    private void checkExistingUser(String email, String phoneNumber) {
        userRepository.findByEmail(email.trim().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("User already exists"); });

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            userRepository.findByPhoneNumber(phoneNumber.trim())
                    .ifPresent(u -> { throw new RuntimeException("Phone already registered"); });
        }
    }

    private User createUser(String firstName, String lastName, String email, String phoneNumber) {
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
        Role role = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void createLocalAuthProvider(User user, String password) {
        // 🔥 DEBUG LOG (VERY IMPORTANT)
        System.out.println("==== DEBUG START ====");
        System.out.println("==== DEBUG END ====");

        UserAuthProvider provider = UserAuthProvider.builder()
                .user(user)
                .provider("LOCAL")
                .passwordHash(passwordEncoder.encode(password))
                .build();

        providerRepository.save(provider);
    }

    private User findUserByEmailOrPhone(String identifier) {
        identifier = identifier.trim();

        Optional<User> userOptional = identifier.contains("@")
                ? userRepository.findByEmailWithRoles(identifier.toLowerCase())
                : userRepository.findByPhoneWithRoles(identifier);

        return userOptional.orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    private void validatePassword(User user, String rawPassword) {

        System.out.println("RAW PASSWORD: " + rawPassword);
        System.out.println("RAW PASSWORD LENGTH: " + rawPassword.length());
        if (rawPassword.length() > 72) {
    throw new RuntimeException("Invalid password input");
}
        UserAuthProvider provider =
                providerRepository.findByUser_IdAndProvider(user.getId(), "LOCAL")
                        .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, provider.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    private void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
}