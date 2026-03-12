package com.aniva.modules.product.mapper;

import com.aniva.modules.product.dto.*;
import com.aniva.modules.product.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponseDTO toResponse(Product product) {

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .brand(product.getBrand())
                .fragranceType(product.getFragranceType())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .finalPrice(calculateFinalPrice(product))
                .discountPercentage(calculateDiscountPercent(product))
                .isActive(product.getIsActive())
                .isDeleted(product.getIsDeleted())
                .categoryName(product.getCategory().getName())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .images(
                        product.getImages()
                                .stream()
                                .map(img -> {
                                    ImageDTO dto = new ImageDTO();
                                    dto.setImageUrl(img.getImageUrl());
                                    dto.setIsPrimary(img.getIsPrimary());
                                    dto.setDisplayOrder(img.getDisplayOrder());
                                    return dto;
                    })
                .toList()
)
                .build();
    }

    private static ImageDTO mapImage(ProductImage image) {

        ImageDTO dto = new ImageDTO();
        dto.setImageUrl(image.getImageUrl());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setDisplayOrder(image.getDisplayOrder());

        return dto;
    }

    private static BigDecimal calculateFinalPrice(Product product) {

        if (product.getDiscountPrice() != null &&
                product.getDiscountPrice().compareTo(product.getPrice()) < 0) {

            return product.getDiscountPrice();
        }

        return product.getPrice();
    }

    private static Integer calculateDiscountPercent(Product product) {

        if (product.getDiscountPrice() == null) return 0;

        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscountPrice();

        return price.subtract(discount)
                .divide(price, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }
}