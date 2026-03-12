package com.aniva.modules.product.entity;

import jakarta.persistence.*;
import lombok.*;

import com.aniva.core.audit.BaseEntity;

@Entity
@Table(name = "categories", schema = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    private Boolean isActive = true;

    private Boolean isDeleted = false;
}