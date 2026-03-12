package com.aniva.modules.product.service;

import com.aniva.modules.product.dto.ReviewDTO;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.entity.ProductReview;
import com.aniva.modules.product.repository.ProductRepository;
import com.aniva.modules.product.repository.ProductReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    // ================= ADD REVIEW =================

    @Override
    public ProductReview addReview(Long productId, ReviewDTO request) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductReview review = ProductReview.builder()
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);

        updateProductRating(product);

        return review;
    }

    // ================= GET REVIEWS =================

    @Override
    public List<ProductReview> getReviews(Long productId) {

        return reviewRepository.findByProductId(productId);
    }

    // ================= UPDATE PRODUCT RATING =================

    private void updateProductRating(Product product) {

        List<ProductReview> reviews =
                reviewRepository.findByProductId(product.getId());

        int reviewCount = reviews.size();

        if (reviewCount == 0) {

            product.setAverageRating(BigDecimal.ZERO);
            product.setReviewCount(0);

            productRepository.save(product);
            return;
        }

        double average =
                reviews.stream()
                        .mapToInt(ProductReview::getRating)
                        .average()
                        .orElse(0);

        product.setAverageRating(BigDecimal.valueOf(average));
        product.setReviewCount(reviewCount);

        productRepository.save(product);
    }
}