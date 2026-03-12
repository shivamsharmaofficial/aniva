package com.aniva.modules.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
}