package com.aniva.modules.user.service;

import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.user.dto.AddressResponse;
import com.aniva.modules.user.dto.WishlistResponse;
import com.aniva.modules.user.entity.UserAddress;

import org.springframework.data.domain.Page;

import java.util.*;

public interface UserService {

    // 🔐 Profile
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse updateProfile(String firstName, String lastName, String phoneNumber);
    void changePassword(String currentPassword, String newPassword);

    // 📍 Address
    List<AddressResponse> getUserAddresses();
    UserAddress addAddress(UserAddress address);
    UserAddress updateAddress(Long id, UserAddress address);
    void deleteAddress(Long id);

    // ❤️ Wishlist
    List<WishlistResponse> getWishlist();
    void addToWishlist(Long productId);
    void removeFromWishlist(Long productId);

    // 📦 Orders
    Page<OrderResponse> getUserOrders(int page, int size);
}