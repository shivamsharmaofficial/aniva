package com.aniva.modules.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemResponse {

    private Long id;

    private Long productId;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

}