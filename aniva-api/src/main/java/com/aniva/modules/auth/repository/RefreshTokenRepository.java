package com.aniva.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aniva.modules.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser_Id(Long userId);
}