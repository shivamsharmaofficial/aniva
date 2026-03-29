package com.aniva.modules.shipping.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShippingAddressResponse {

    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String pincode;
}
