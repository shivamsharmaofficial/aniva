package com.aniva.modules.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.aniva.modules.product.dto.CategoryResponseDTO;
import com.aniva.modules.product.entity.Category;
import com.aniva.modules.product.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
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