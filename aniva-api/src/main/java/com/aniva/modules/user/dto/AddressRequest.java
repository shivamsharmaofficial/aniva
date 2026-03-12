package com.aniva.modules.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private Boolean isDefault;
}