package com.aniva.modules.user.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponse {

    private Long productId;
    private String productName;     // optional for later
    private String imageUrl;        // optional for later
    private BigDecimal price;           // optional for later
}