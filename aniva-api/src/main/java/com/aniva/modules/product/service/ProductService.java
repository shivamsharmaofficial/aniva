package com.aniva.modules.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.aniva.modules.product.dto.CreateProductRequestDTO;
import com.aniva.modules.product.dto.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO createProduct(CreateProductRequestDTO request);

    ProductResponseDTO updateProduct(Long id, CreateProductRequestDTO request);

    void deleteProduct(Long id);

    ProductResponseDTO getBySlug(String slug);

    ProductResponseDTO getById(Long id);

    void restoreProduct(Long id);

    void toggleActive(Long id);

    Page<ProductResponseDTO> getProducts(
            List<String> category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String search,
            String status,
            Boolean includeDeleted,
            String sort,
            String direction,
            int page,
            int size
    );
}