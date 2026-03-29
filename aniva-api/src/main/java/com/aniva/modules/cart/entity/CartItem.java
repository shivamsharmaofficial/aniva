package com.aniva.modules.cart.entity;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cart_items",
        schema = "cart",
        indexes = {
                @Index(name = "idx_cart_product", columnList = "cart_id, product_id")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_item_cart_product",
                columnNames = {"cart_id", "product_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
}
