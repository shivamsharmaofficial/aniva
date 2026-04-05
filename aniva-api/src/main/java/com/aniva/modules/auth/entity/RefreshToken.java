package com.aniva.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.aniva.core.audit.BaseEntity;

@Entity
@Table(name = "refresh_tokens", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean revoked = false;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;

    // 🔥 DEVICE TRACKING (NEW FIELDS)
    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;
}