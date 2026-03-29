package com.aniva.modules.shipping.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignShipmentRequest {

    private Long orderId;
    private String trackingNumber;
    private String courier;
    private String status;
}
