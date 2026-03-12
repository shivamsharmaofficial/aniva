package com.aniva.modules.auth.entity;

import com.aniva.core.audit.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity{

    @Column(name = "role_name", unique = true)
    private String roleName;
}