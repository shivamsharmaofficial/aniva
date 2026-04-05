package com.aniva.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aniva.modules.auth.entity.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findAllByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
    
    Optional<RefreshToken> findByUser_IdAndTokenIdAndRevokedFalse(
        Long userId,
        String tokenId
);
}