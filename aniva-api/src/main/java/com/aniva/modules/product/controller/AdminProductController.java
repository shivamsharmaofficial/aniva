package com.aniva.modules.product.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.product.dto.CreateProductRequestDTO;
import com.aniva.modules.product.dto.ProductResponseDTO;
import com.aniva.modules.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponseDTO> createProduct(
            @Valid @RequestBody CreateProductRequestDTO request) {

        return ApiResponse.success(
                "Product created successfully",
                productService.createProduct(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequestDTO request) {

        return ApiResponse.success(
                "Product updated successfully",
                productService.updateProduct(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Product soft deleted", null);
    }

    @PatchMapping("/{id}/restore")
    public ApiResponse<Void> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ApiResponse.success("Product restored", null);
    }

    @PatchMapping("/{id}/toggle-active")
    public ApiResponse<Void> toggleActive(@PathVariable Long id) {
        productService.toggleActive(id);
        return ApiResponse.success("Status updated", null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDTO> getProductById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Product fetched",
                productService.getById(id)
        );
    }

    @GetMapping
    public ApiResponse<Page<ProductResponseDTO>> getAdminProducts(
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                "Admin products fetched",
                productService.getProducts(
                        category,
                        minPrice,
                        maxPrice,
                        search,
                        status,
                        includeDeleted != null ? includeDeleted : false,
                        sort,
                        direction,
                        page,
                        size
                )
        );
    }
}
