package com.aniva.modules.product.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.product.dto.CategoryResponseDTO;
import com.aniva.modules.product.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponseDTO>> getAllCategories() {
        try {
            List<CategoryResponseDTO> categories = categoryService.getAllActiveCategories();

            return ApiResponse.success(
                    "Categories fetched successfully",
                    categories == null ? List.of() : categories
            );
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponse.success(
                    "Categories fetched successfully",
                    List.of()
            );
        }
    }
}
