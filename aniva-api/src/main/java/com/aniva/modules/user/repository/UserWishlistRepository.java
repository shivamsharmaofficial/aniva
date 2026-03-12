package com.aniva.modules.user.repository;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.user.entity.UserWishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWishlistRepository extends JpaRepository<UserWishlist, Long> {

    List<UserWishlist> findByUser(User user);

    Optional<UserWishlist> findByUserAndProductId(User user, Long productId);

    void deleteByUserAndProductId(User user, Long productId);
}