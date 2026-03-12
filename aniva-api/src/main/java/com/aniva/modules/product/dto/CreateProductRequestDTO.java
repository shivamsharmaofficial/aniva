package com.aniva.modules.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequestDTO {

    private String name;
    private String slug;
    private String description;
    private String brand;
    private String fragranceType;
    private Integer burnTime;
    private Integer weightGrams;

    private BigDecimal price;
    private BigDecimal discountPrice;

    private Long categoryId;

    // ===== ENTERPRISE FIELDS =====

    private List<ProductImageDTO> images;
    private List<ProductAttributeDTO> attributes;
    private List<ProductVariantDTO> variants;

    // ================= IMAGE DTO =================

    @Data
    public static class ProductImageDTO {
        private String imageUrl;
        private Boolean isPrimary;
        private Integer displayOrder;
    }

    // ================= ATTRIBUTE DTO =================

    @Data
    public static class ProductAttributeDTO {
        private String attributeName;
        private String attributeValue;
    }

    // ================= VARIANT DTO =================

    @Data
    public static class ProductVariantDTO {
        private String variantName;
        private String sku;
        private BigDecimal variantPrice;
        private Integer stockQuantity;
        private Integer weightGrams;
    }
}