package com.aniva.modules.product.service;

import com.aniva.modules.product.dto.ReviewDTO;
import com.aniva.modules.product.entity.ProductReview;

import java.util.List;
import java.util.UUID;

public interface ProductReviewService {

    ProductReview addReview(Long productId, ReviewDTO request);

    List<ProductReview> getReviews(Long productId);

}