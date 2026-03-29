package com.aniva.modules.shipping.dto;

import com.aniva.modules.shipping.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentResponse {

    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String courier;
    private ShipmentStatus status;
}
