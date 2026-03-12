package com.aniva.modules.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {

    private String identifier;
    private String password;

}