package com.aniva.modules.product.service;

import com.aniva.modules.product.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDTO> getAllActiveCategories();
}