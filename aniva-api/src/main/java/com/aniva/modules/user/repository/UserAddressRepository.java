package com.aniva.modules.user.repository;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.user.entity.UserAddress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUser(User user);

    Optional<UserAddress> findByIdAndUser(Long id, User user);

    Optional<UserAddress> findByUserAndIsDefaultTrue(User user);
}