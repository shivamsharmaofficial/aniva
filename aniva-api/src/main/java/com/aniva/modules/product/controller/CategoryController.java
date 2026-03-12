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

        return ApiResponse.success(
                "Categories fetched successfully",
                categoryService.getAllActiveCategories()
        );
    }
}