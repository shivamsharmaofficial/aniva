package com.aniva.modules.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_events", schema = "analytics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String eventType;   // LOGIN_SUCCESS, REGISTER_SUCCESS

    private String entityType;  // AUTH

    private String entityId;    // optional (userId or email)

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private String deviceInfo;

    private String ipAddress;

    private LocalDateTime createdAt = LocalDateTime.now();
}