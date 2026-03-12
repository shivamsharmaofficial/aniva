package com.aniva.modules.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

import com.aniva.core.audit.BaseEntity;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "products", schema = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String brand;

    private String fragranceType;

    private Integer burnTime;

    private Integer weightGrams;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discountPrice;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    private BigDecimal averageRating = BigDecimal.ZERO;

    private Integer reviewCount = 0;

    @Column(name = "reserved_stock")
    private Integer reservedStock = 0;

    @Column(name = "total_stock")
    private Integer totalStock = 0;

    /*
     * Category relationship
     * Explicitly set LAZY to avoid unnecessary joins
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /*
     * Product Images
     * LAZY loading + BatchSize prevents N+1 queries
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<ProductImage> images = new ArrayList<>();

    /*
     * Product Reviews
     * Also batch fetched
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<ProductReview> reviews = new ArrayList<>();
}