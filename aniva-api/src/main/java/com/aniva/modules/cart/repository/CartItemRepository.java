package com.aniva.modules.cart.repository;

import com.aniva.modules.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
            SELECT DISTINCT ci
            FROM CartItem ci
            JOIN FETCH ci.product p
            LEFT JOIN FETCH p.images
            WHERE ci.cart.id = :cartId
            """)
    List<CartItem> findDetailedByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProduct_Id(Long cartId, Long productId);
}
