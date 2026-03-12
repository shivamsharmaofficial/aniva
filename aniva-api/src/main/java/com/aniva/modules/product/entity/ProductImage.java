package com.aniva.modules.product.entity;

import com.aniva.core.audit.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images", schema = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends BaseEntity {

    @Column(nullable = false)
    private String imageUrl;

    private Boolean isPrimary = false;

    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}