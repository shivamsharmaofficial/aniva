package com.aniva.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.auth.entity.User;

@Entity
@Table(schema =  "\"user\"", name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {

    // 🔐 Relationship with auth.users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
    private String addressLine;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String pincode;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}