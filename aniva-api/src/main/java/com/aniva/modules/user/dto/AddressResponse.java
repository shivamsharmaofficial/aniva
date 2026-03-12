package com.aniva.modules.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponse {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private Boolean isDefault;
}