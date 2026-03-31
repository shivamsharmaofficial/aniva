package com.aniva.modules.cart.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.core.security.CustomUserDetails;
import com.aniva.modules.cart.dto.AddToCartRequest;
import com.aniva.modules.cart.dto.CartItemResponse;
import com.aniva.modules.cart.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /* ================= GET CART ================= */

    @GetMapping
    public ApiResponse<List<CartItemResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        // ✅ Null check FIRST
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // ✅ Role check
        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot access cart");
        }

        return ApiResponse.success(
                "Cart fetched successfully",
                cartService.getCart(userDetails.getUserId())
        );
    }

    /* ================= ADD TO CART ================= */

    @PostMapping("/add")
    public ApiResponse<Void> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AddToCartRequest request
    ) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot add items to cart");
        }

        cartService.addToCart(userDetails.getUserId(), request);

        return ApiResponse.success(
                "Product added to cart",
                null
        );
    }

    /* ================= REMOVE ITEM ================= */

    @DeleteMapping("/items/{productId}")
    public ApiResponse<Void> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        cartService.removeProduct(userDetails.getUserId(), productId);

        return ApiResponse.success(
                "Item removed from cart",
                null
        );
    }
}
