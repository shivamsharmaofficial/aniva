package com.aniva.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aniva.modules.auth.entity.UserAuthProvider;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    // used in UserServiceImpl
    Optional<UserAuthProvider> findByUserIdAndProvider(Long userId, String provider);

    // used in AuthServiceImpl
    Optional<UserAuthProvider> findByUser_IdAndProvider(Long userId, String provider);

}