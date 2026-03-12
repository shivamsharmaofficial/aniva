package com.aniva.modules.cart.dto;

import lombok.Data;

@Data
public class AddToCartRequest {

    private Long productId;
    private Integer quantity;

}