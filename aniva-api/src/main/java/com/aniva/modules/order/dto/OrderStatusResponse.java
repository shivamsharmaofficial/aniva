package com.aniva.modules.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderStatusResponse {

    private Long orderId;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
