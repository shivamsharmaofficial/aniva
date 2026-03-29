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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final int CART_UPDATE_MAX_RETRIES = 3;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final TransactionTemplate transactionTemplate;

    /* ================= GET CART ================= */

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElse(null);

        if (cart == null) {
            return List.of();
        }

        List<CartItem> items = cartItemRepository.findDetailedByCartId(cart.getId());

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

        validateAddToCartRequest(request);

        for (int attempt = 1; attempt <= CART_UPDATE_MAX_RETRIES; attempt++) {
            try {
                transactionTemplate.executeWithoutResult(
                        status -> addToCartInTransaction(userId, request)
                );
                return;
            } catch (ObjectOptimisticLockingFailureException
                     | DataIntegrityViolationException ex) {

                if (attempt == CART_UPDATE_MAX_RETRIES) {
                    throw new RuntimeException(
                            "Cart is being updated by another request. Please try again.",
                            ex
                    );
                }
            }
        }
    }

    @Transactional
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

    @Transactional
    public void removeProduct(Long userId, Long productId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository.findByCartIdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElse(null);

        if (cart == null) {
            return;
        }

        List<CartItem> items = cartItemRepository.findDetailedByCartId(cart.getId());
        cartItemRepository.deleteAll(items);
    }

    /* ================= CREATE CART ================= */

    @Transactional
    void addToCartInTransaction(Long userId, AddToCartRequest request) {

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findByIdForUpdate(request.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found"));

        int available = product.getTotalStock() - product.getReservedStock();

        if (available < request.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProduct_Id(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            return;
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(item);
    }

    private Cart getOrCreateCart(Long userId) {

        return cartRepository.findByUser_Id(userId)
                .orElseGet(() -> createCart(userId));
    }

    private Cart createCart(Long userId) {

        User user = new User();
        user.setId(userId);

        try {
            Cart cart = Cart.builder()
                    .user(user)
                    .build();

            return cartRepository.save(cart);
        } catch (DataIntegrityViolationException ex) {
            return cartRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found", ex));
        }
    }

    private void validateAddToCartRequest(AddToCartRequest request) {

        if (request.getProductId() == null) {
            throw new RuntimeException("Product ID is required");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }
    }
}
