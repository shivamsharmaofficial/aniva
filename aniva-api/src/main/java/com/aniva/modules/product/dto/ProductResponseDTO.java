package com.aniva.modules.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String brand;

    private String fragranceType;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private BigDecimal finalPrice;

    private Integer discountPercentage;

    private Integer totalStock;

    private String stockStatus;

    private Boolean isActive;

    private Boolean isDeleted;

    private String categoryName;

    private BigDecimal averageRating;

    private Integer reviewCount;

    private List<ImageDTO> images;
}