package com.aniva.modules.product.service;

import com.aniva.modules.product.dto.CreateProductRequestDTO;
import com.aniva.modules.product.dto.ProductResponseDTO;
import com.aniva.modules.product.entity.Category;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.entity.ProductImage;
import com.aniva.modules.product.mapper.ProductMapper;
import com.aniva.modules.product.repository.CategoryRepository;
import com.aniva.modules.product.repository.ProductRepository;
import com.aniva.modules.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_LIST_CACHE = "product-list";
    private static final String PRODUCT_SINGLE_CACHE = "product-single";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectProvider<ProductServiceImpl> selfProvider;
    private final CacheManager cacheManager;

    // CREATE PRODUCT

    @Override
    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
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
    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    public ProductResponseDTO updateProduct(Long id, CreateProductRequestDTO request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String previousSlug = product.getSlug();

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

        ProductResponseDTO response = ProductMapper.toResponse(product);
        evictSingleProductCaches(previousSlug, response.getSlug());

        return response;
    }

    // DELETE PRODUCT

    @Override
    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsDeleted(true);
        product.setIsActive(false);
        evictSingleProductCache(product.getSlug());
    }

    // GET PRODUCT BY SLUG

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = PRODUCT_SINGLE_CACHE,
            key = "T(com.aniva.modules.product.service.ProductServiceImpl).buildSingleProductCacheKey(#slug)",
            unless = "#result == null"
    )
    public ProductResponseDTO getBySlug(String slug) {

        Product product = productRepository
                .findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDTO getById(Long id) {

        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    // RESTORE PRODUCT

    @Override
    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    public void restoreProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsDeleted(false);
        product.setIsActive(true);
        evictSingleProductCache(product.getSlug());
    }

    @Override
    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    public void toggleActive(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(!product.getIsActive());
        evictSingleProductCache(product.getSlug());
    }

    // FILTER PRODUCTS

    @Override
    @Transactional(readOnly = true)
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

        List<String> safeCategorySlugs = normalizeCategorySlugs(categorySlugs);

        Pageable pageable = buildPageable(sort, direction, page, size);

        ProductListCacheValue cachedPage = selfProvider.getObject().getCachedProducts(
                safeCategorySlugs,
                minPrice,
                maxPrice,
                search,
                status,
                includeDeleted,
                sort,
                direction,
                page,
                size
        );

        return new PageImpl<>(cachedPage.content(), pageable, cachedPage.totalElements());
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = PRODUCT_LIST_CACHE,
            key = "T(com.aniva.modules.product.service.ProductServiceImpl).buildProductsCacheKey(" +
                    "#categorySlugs, #minPrice, #maxPrice, #search, #status, #includeDeleted, " +
                    "#sort, #direction, #page, #size)",
            unless = "#result == null || #result.content == null || #result.content.isEmpty()"
    )
    public ProductListCacheValue getCachedProducts(
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

        List<String> safeCategorySlugs = normalizeCategorySlugs(categorySlugs);

        Pageable pageable = buildPageable(sort, direction, page, size);

        Page<ProductResponseDTO> productsPage = productRepository.findAll(
                ProductSpecification.filter(
                        safeCategorySlugs,
                        minPrice,
                        maxPrice,
                        search,
                        status,
                        includeDeleted
                ),
                pageable
        ).map(ProductMapper::toResponse);

        return new ProductListCacheValue(
                productsPage.getContent(),
                productsPage.getTotalElements()
        );
    }

    public static String buildProductsCacheKey(
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

        List<String> normalizedCategories = categorySlugs == null
                ? List.of()
                : categorySlugs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(value -> !value.isEmpty())
                .sorted()
                .toList();

        StringJoiner joiner = new StringJoiner("|", "products:v2|", "");
        joiner.add("page=" + Math.max(page, 0));
        joiner.add("size=" + Math.max(size, 1));
        joiner.add("categories=" + String.join(",", normalizedCategories));
        joiner.add("minPrice=" + normalizeDecimal(minPrice));
        joiner.add("maxPrice=" + normalizeDecimal(maxPrice));
        joiner.add("search=" + normalizeText(search));
        joiner.add("status=" + normalizeText(status));
        joiner.add("includeDeleted=" + Boolean.TRUE.equals(includeDeleted));
        joiner.add("sort=" + normalizeSortField(sort));
        joiner.add("direction=" + normalizeDirection(direction));

        return joiner.toString();
    }

    public static String buildSingleProductCacheKey(String slug) {
        return "product:v1|slug=" + normalizeText(slug);
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

    private Pageable buildPageable(String sort, String direction, int page, int size) {

        return PageRequest.of(
                page,
                size,
                Sort.by(
                        "asc".equalsIgnoreCase(direction)
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        mapSortField(sort)
                )
        );
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

    private void evictSingleProductCaches(String... slugs) {

        Cache cache = cacheManager.getCache(PRODUCT_SINGLE_CACHE);

        if (cache == null) {
            return;
        }

        for (String slug : slugs) {
            if (slug != null && !slug.isBlank()) {
                cache.evict(buildSingleProductCacheKey(slug));
            }
        }
    }

    private void evictSingleProductCache(String slug) {
        evictSingleProductCaches(slug);
    }

    private static String normalizeDecimal(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private static String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static String normalizeSortField(String sort) {
        return sort == null ? "createdat" : sort.trim().toLowerCase();
    }

    private static String normalizeDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) ? "asc" : "desc";
    }

    private List<String> normalizeCategorySlugs(List<String> categorySlugs) {
        if (categorySlugs == null || categorySlugs.isEmpty()) {
            return Collections.emptyList();
        }

        return categorySlugs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    public record ProductListCacheValue(
            List<ProductResponseDTO> content,
            long totalElements
    ) {
    }
}
