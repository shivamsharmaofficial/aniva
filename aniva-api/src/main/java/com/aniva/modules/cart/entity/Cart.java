package com.aniva.modules.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.auth.entity.User;

@Entity
@Table(name = "carts", schema = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}