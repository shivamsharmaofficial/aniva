package com.aniva.modules.auth.service;

import com.aniva.modules.auth.dto.AuthResponse;
import com.aniva.modules.auth.dto.UserProfileResponse;

public interface AuthService {

    AuthResponse register(String firstName,
                          String lastName,
                          String email,
                          String phoneNumber,
                          String password);

    AuthResponse login(String identifier,
                       String password);

    UserProfileResponse getCurrentUser();

    AuthResponse refreshToken(String refreshToken);
}