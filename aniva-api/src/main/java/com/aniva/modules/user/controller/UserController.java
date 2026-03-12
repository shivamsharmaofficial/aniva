package com.aniva.modules.user.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.user.dto.AddressRequest;
import com.aniva.modules.user.dto.AddressResponse;
import com.aniva.modules.user.dto.ChangePasswordRequest;
import com.aniva.modules.user.dto.UpdateProfileRequest;
import com.aniva.modules.user.dto.WishlistResponse;
import com.aniva.modules.user.entity.UserAddress;
import com.aniva.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =========================
    // 🔐 GET PROFILE
    // =========================
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {

        UserProfileResponse profile = userService.getCurrentUserProfile();

        ApiResponse<UserProfileResponse> response =
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully")
                        .data(profile)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ✏ UPDATE PROFILE
    // =========================
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request) {

        UserProfileResponse updated = userService.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
        );

        ApiResponse<UserProfileResponse> response =
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Profile updated successfully")
                        .data(updated)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // 🔑 CHANGE PASSWORD
    // =========================
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // 📍 GET ADDRESSES
    // =========================
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses() {

        List<AddressResponse> addresses = userService.getUserAddresses();

        ApiResponse<List<AddressResponse>> response =
            ApiResponse.<List<AddressResponse>>builder()
                .success(true)
                .message("Addresses fetched successfully")
                .data(addresses)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ➕ ADD ADDRESS
    // =========================
    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<UserAddress>> addAddress(
            @RequestBody AddressRequest request) {

        UserAddress address = UserAddress.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .isDefault(request.getIsDefault())
                .build();

        UserAddress saved = userService.addAddress(address);

        ApiResponse<UserAddress> response =
                ApiResponse.<UserAddress>builder()
                        .success(true)
                        .message("Address added successfully")
                        .data(saved)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ✏ UPDATE ADDRESS
    // =========================
    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<UserAddress>> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequest request) {

        UserAddress address = UserAddress.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .isDefault(request.getIsDefault())
                .build();

        UserAddress updated = userService.updateAddress(id, address);

        ApiResponse<UserAddress> response =
                ApiResponse.<UserAddress>builder()
                        .success(true)
                        .message("Address updated successfully")
                        .data(updated)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ❌ DELETE ADDRESS
    // =========================
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id) {

        userService.deleteAddress(id);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Address deleted successfully")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }


    // =========================
    // ❤️ GET WISHLIST
    // =========================
    @GetMapping("/wishlist")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist() {

        List<WishlistResponse> wishlist = userService.getWishlist();

        ApiResponse<List<WishlistResponse>> response =
                ApiResponse.<List<WishlistResponse>>builder()
                        .success(true)
                        .message("Wishlist fetched successfully")
                        .data(wishlist)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ➕ ADD TO WISHLIST
    // =========================
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
            @PathVariable Long productId) {

        userService.addToWishlist(productId);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Added to wishlist")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // ❌ REMOVE FROM WISHLIST
    // =========================
    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable Long productId) {

        userService.removeFromWishlist(productId);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Removed from wishlist")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    // =========================
    // 📦 GET USER ORDERS (PAGINATED)
    // =========================
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<OrderResponse> orders = userService.getUserOrders(page, size);

        ApiResponse<Page<OrderResponse>> response =
                ApiResponse.<Page<OrderResponse>>builder()
                        .success(true)
                        .message("Orders fetched successfully")
                        .data(orders)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }
}