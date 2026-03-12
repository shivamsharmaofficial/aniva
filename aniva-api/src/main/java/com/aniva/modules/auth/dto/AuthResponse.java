package com.aniva.modules.auth.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
}