package com.aniva.modules.user.service;

import com.aniva.modules.auth.dto.UserProfileResponse;
import com.aniva.modules.auth.entity.User;
import com.aniva.modules.auth.entity.UserAuthProvider;
import com.aniva.modules.auth.repository.UserAuthProviderRepository;
import com.aniva.modules.auth.repository.UserRepository;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.user.dto.AddressResponse;
import com.aniva.modules.user.dto.WishlistResponse;
import com.aniva.modules.user.entity.UserAddress;
import com.aniva.modules.user.entity.UserWishlist;
import com.aniva.modules.user.repository.UserAddressRepository;
import com.aniva.modules.user.repository.UserWishlistRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository providerRepository;
    private final UserAddressRepository addressRepository;
    private final UserWishlistRepository wishlistRepository;
    private final UserOrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // 🔐 PROFILE
    // =========================

    @Override
    public UserProfileResponse getCurrentUserProfile() {

        User user = getAuthenticatedUser();

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getRoleName())
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .status(user.getStatus())
                .roles(roles)
                .build();
    }

    @Override
    public UserProfileResponse updateProfile(String firstName,
                                             String lastName,
                                             String phoneNumber) {

        User user = getAuthenticatedUser();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return getCurrentUserProfile();
    }

    @Override
    public void changePassword(String currentPassword,
                               String newPassword) {

        User user = getAuthenticatedUser();

        UserAuthProvider provider = providerRepository
                .findByUserIdAndProvider(user.getId(), "LOCAL")
                .orElseThrow(() -> new RuntimeException("Local provider not found"));

        if (!passwordEncoder.matches(currentPassword, provider.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        provider.setPasswordHash(passwordEncoder.encode(newPassword));
        provider.setUpdatedAt(LocalDateTime.now());

        providerRepository.save(provider);
    }

    // =========================
    // 🔒 HELPER
    // =========================

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // 📍 ADDRESS MANAGEMENT
    // =========================

    @Override
    public List<AddressResponse> getUserAddresses() {

        User user = getAuthenticatedUser();

        return addressRepository.findByUser(user)
                .stream()
                .map(address -> AddressResponse.builder()
                        .id(address.getId())
                        .fullName(address.getFullName())
                        .phoneNumber(address.getPhoneNumber())
                        .addressLine(address.getAddressLine())
                        .city(address.getCity())
                        .state(address.getState())
                        .pincode(address.getPincode())
                        .isDefault(address.getIsDefault())
                        .build()
                )
                .toList();
    }

    @Override
    public UserAddress addAddress(UserAddress address) {

        User user = getAuthenticatedUser();

        // If new address is default → unset previous default
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        addressRepository.save(existing);
                    });
        }

        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public UserAddress updateAddress(Long id, UserAddress updatedAddress) {

        User user = getAuthenticatedUser();

        UserAddress existing = addressRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        existing.setFullName(updatedAddress.getFullName());
        existing.setPhoneNumber(updatedAddress.getPhoneNumber());
        existing.setAddressLine(updatedAddress.getAddressLine());
        existing.setCity(updatedAddress.getCity());
        existing.setState(updatedAddress.getState());
        existing.setPincode(updatedAddress.getPincode());

        if (Boolean.TRUE.equals(updatedAddress.getIsDefault())) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(defaultAddr -> {
                        defaultAddr.setIsDefault(false);
                        addressRepository.save(defaultAddr);
                    });
            existing.setIsDefault(true);
        }

        return addressRepository.save(existing);
    }

    @Override
    public void deleteAddress(Long id) {

        User user = getAuthenticatedUser();

        UserAddress address = addressRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);
    }

    // =========================
    // ❤️ WISHLIST MANAGEMENT
    // =========================

    @Override
    public List<WishlistResponse> getWishlist() {

        User user = getAuthenticatedUser();

        return wishlistRepository.findByUser(user)
                .stream()
                .map(item -> WishlistResponse.builder()
                        .productId(item.getProductId())
                        .build()
                )
                .toList();
    }

    @Override
    public void addToWishlist(Long productId) {

        User user = getAuthenticatedUser();

        if (wishlistRepository.findByUserAndProductId(user, productId).isPresent()) {
                throw new RuntimeException("Product already in wishlist");
        }

        UserWishlist wishlist = UserWishlist.builder()
                .user(user)
                .productId(productId)
                .build();

        wishlistRepository.save(wishlist);
    }

    @Override
    public void removeFromWishlist(Long productId) {

        User user = getAuthenticatedUser();

        UserWishlist wishlist = wishlistRepository
                .findByUserAndProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        wishlistRepository.delete(wishlist);
    }

    // =========================
    // 📦 ORDER HISTORY
    // =========================

    @Override
    public Page<OrderResponse> getUserOrders(int page, int size) {

        User user = getAuthenticatedUser();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<UserOrder> orderPage =
                orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return orderPage.map(order ->
                OrderResponse.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .createdAt(order.getCreatedAt())
                        .build()
        );
    }

}