package com.aniva.modules.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {

    private Long productId;

    private Integer rating;

    private String comment;

}