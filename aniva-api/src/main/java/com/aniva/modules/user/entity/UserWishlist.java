package com.aniva.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.auth.entity.User;

@Entity
@Table(
        schema =  "\"user\"",
        name = "user_wishlist",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "product_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWishlist extends BaseEntity {

    // 🔐 Link to auth.users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // For now simple productId (later can map to Product entity)
   @Column(name = "product_id", nullable = false)
    private Long productId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}