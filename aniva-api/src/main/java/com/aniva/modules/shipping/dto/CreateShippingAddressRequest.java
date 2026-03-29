package com.aniva.modules.shipping.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShippingAddressRequest {

    private String name;
    private String phone;
    private String address;
    private String city;
    private String pincode;
}
