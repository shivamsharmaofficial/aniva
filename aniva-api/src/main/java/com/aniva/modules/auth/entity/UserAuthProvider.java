package com.aniva.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.aniva.core.audit.BaseEntity;

@Entity
@Table(name = "user_auth_providers", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthProvider extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String provider;

    @Column(name = "password_hash")
    private String passwordHash;
}