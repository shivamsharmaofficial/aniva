package com.aniva.modules.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
}