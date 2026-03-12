package com.aniva.modules.product.repository;

import com.aniva.modules.product.entity.ProductImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);

}