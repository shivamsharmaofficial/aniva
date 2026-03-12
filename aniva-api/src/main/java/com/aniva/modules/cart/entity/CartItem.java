package com.aniva.modules.cart.entity;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items", schema = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
}