package com.aniva.modules.cart.service;

import com.aniva.modules.cart.dto.AddToCartRequest;
import com.aniva.modules.cart.dto.CartItemResponse;
import com.aniva.modules.cart.entity.Cart;
import com.aniva.modules.cart.entity.CartItem;
import com.aniva.modules.cart.repository.CartItemRepository;
import com.aniva.modules.cart.repository.CartRepository;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.repository.ProductRepository;
import com.aniva.modules.auth.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /* ================= GET CART ================= */

    public List<CartItemResponse> getCart(Long userId) {

    Cart cart = cartRepository.findByUser_Id(userId)
            .orElse(null);

    if (cart == null) {
        return List.of();
    }

    List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

    return items.stream()
            .map(item -> {

                Product product = item.getProduct();

                String imageUrl = null;

                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    imageUrl = product.getImages().get(0).getImageUrl();
                }

                return new CartItemResponse(
                        item.getId(),
                        product.getId(),
                        product.getName(),
                        imageUrl,
                        product.getPrice().doubleValue(),
                        item.getQuantity()
                );

            })
            .toList();
    }

    /* ================= ADD TO CART ================= */

    public void addToCart(Long userId, AddToCartRequest request) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cartItemRepository
                .findByCartIdAndProduct_Id(cart.getId(), product.getId())
                .orElse(null);

        if (existing != null) {

            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemRepository.save(existing);
            return;
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(item);
    }

    /* ================= CREATE CART ================= */

    private Cart createCart(Long userId) {

        User user = new User();
        user.setId(userId);

        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(cart);
    }

    /* ================= REMOVE ITEM ================= */

    public void removeItem(Long userId, Long itemId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user cart");
        }

        cartItemRepository.delete(item);
    }
}