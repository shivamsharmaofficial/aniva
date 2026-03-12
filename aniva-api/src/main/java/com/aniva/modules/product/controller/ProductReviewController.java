package com.aniva.modules.product.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.product.dto.ReviewDTO;
import com.aniva.modules.product.entity.ProductReview;
import com.aniva.modules.product.service.ProductReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @PostMapping("/{productId}/reviews")
    public ApiResponse<ProductReview> addReview(
            @PathVariable Long productId,
            @RequestBody ReviewDTO request
    ) {

        return ApiResponse.success(
                "Review added successfully",
                reviewService.addReview(productId, request)
        );
    }

    @GetMapping("/{productId}/reviews")
    public ApiResponse<List<ProductReview>> getReviews(
            @PathVariable Long productId
    ) {

        return ApiResponse.success(
                "Reviews fetched successfully",
                reviewService.getReviews(productId)
        );
    }
}