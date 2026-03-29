package com.aniva.modules.product.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.product.dto.ProductResponseDTO;
import com.aniva.modules.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return ApiResponse.success(
                "Products fetched successfully",
                productService.getProducts(
                        category,
                        minPrice,
                        maxPrice,
                        search,
                        "ACTIVE",
                        false,
                        sort,
                        direction,
                        page,
                        size
                )
        );
    }

    @GetMapping("/{slug}")
    public ApiResponse<ProductResponseDTO> getProductBySlug(
            @PathVariable String slug) {

        return ApiResponse.success(
                "Product fetched successfully",
                productService.getBySlug(slug)
        );
    }
}