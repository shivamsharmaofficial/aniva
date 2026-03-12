package com.aniva.modules.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aniva.modules.product.dto.CreateProductRequestDTO;
import com.aniva.modules.product.dto.ProductResponseDTO;
import com.aniva.modules.product.entity.*;
import com.aniva.modules.product.mapper.ProductMapper;
import com.aniva.modules.product.repository.CategoryRepository;
import com.aniva.modules.product.repository.ProductRepository;
import com.aniva.modules.product.specification.ProductSpecification;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // CREATE PRODUCT

    @Override
    public ProductResponseDTO createProduct(CreateProductRequestDTO request) {

        validatePrice(request.getPrice(), request.getDiscountPrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String slug = generateUniqueSlug(request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .brand(request.getBrand())
                .fragranceType(request.getFragranceType())
                .burnTime(request.getBurnTime())
                .weightGrams(request.getWeightGrams())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .category(category)
                .isDeleted(false)
                .isActive(true)
                .build();

        handleImages(product, request);

        productRepository.save(product);

        return ProductMapper.toResponse(product);
    }

    // UPDATE PRODUCT

    @Override
    public ProductResponseDTO updateProduct(Long id, CreateProductRequestDTO request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validatePrice(request.getPrice(), request.getDiscountPrice());

        if (!product.getName().equals(request.getName())) {
            product.setSlug(generateUniqueSlug(request.getName()));
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setFragranceType(request.getFragranceType());
        product.setBurnTime(request.getBurnTime());
        product.setWeightGrams(request.getWeightGrams());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());

        if (!product.getCategory().getId().equals(request.getCategoryId())) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        if (request.getImages() != null) {

            product.getImages().clear();
            handleImages(product, request);
        }

        return ProductMapper.toResponse(product);
    }

    // DELETE PRODUCT

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsDeleted(true);
        product.setIsActive(false);
    }

    // GET PRODUCT BY SLUG

    @Override
    public ProductResponseDTO getBySlug(String slug) {

        Product product = productRepository
                .findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDTO getById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    // RESTORE PRODUCT

    @Override
    public void restoreProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsDeleted(false);
        product.setIsActive(true);
    }

    @Override
    public void toggleActive(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(!product.getIsActive());
    }

    // FILTER PRODUCTS

    @Override
    public Page<ProductResponseDTO> getProducts(
            List<String> categorySlugs,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String search,
            String status,
            Boolean includeDeleted,
            String sort,
            String direction,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        "asc".equalsIgnoreCase(direction)
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        mapSortField(sort)
                )
        );

        return productRepository.findAll(
                ProductSpecification.filter(
                        categorySlugs,
                        minPrice,
                        maxPrice,
                        search,
                        status,
                        includeDeleted
                ),
                pageable
        ).map(ProductMapper::toResponse);
    }

    // SORT MAPPING

    private String mapSortField(String sort) {

        Map<String, String> allowedSorts = Map.of(
                "price", "price",
                "name", "name",
                "createdAt", "createdAt"
        );

        if (sort == null || !allowedSorts.containsKey(sort)) {
            return "createdAt";
        }

        return allowedSorts.get(sort);
    }

    // IMAGE HANDLER

    private void handleImages(Product product, CreateProductRequestDTO request) {

        if (request.getImages() == null) return;

        request.getImages().forEach(imgDTO -> {

            ProductImage image = ProductImage.builder()
                    .imageUrl(imgDTO.getImageUrl())
                    .isPrimary(imgDTO.getIsPrimary())
                    .displayOrder(imgDTO.getDisplayOrder())
                    .product(product)
                    .build();

            product.getImages().add(image);
        });
    }

    // PRICE VALIDATION

    private void validatePrice(BigDecimal price, BigDecimal discountPrice) {

        if (discountPrice != null && price != null) {

            if (discountPrice.compareTo(price) >= 0) {

                throw new RuntimeException(
                        "Discount price must be less than actual price"
                );
            }
        }
    }

    // SLUG GENERATOR

    private String generateSlug(String input) {

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }

    private String generateUniqueSlug(String name) {

        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }
}