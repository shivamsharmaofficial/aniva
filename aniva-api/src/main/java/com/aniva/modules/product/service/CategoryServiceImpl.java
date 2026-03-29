package com.aniva.modules.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.aniva.modules.product.dto.CategoryResponseDTO;
import com.aniva.modules.product.entity.Category;
import com.aniva.modules.product.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_CACHE = "categories";

    private final CategoryRepository categoryRepository;

    @Override
    @Cacheable(cacheNames = CATEGORY_CACHE, key = "'active'")
    public List<CategoryResponseDTO> getAllActiveCategories() {

        return categoryRepository
                .findByIsActiveTrueAndIsDeletedFalse()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private CategoryResponseDTO mapToDTO(Category category) {

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }
}
