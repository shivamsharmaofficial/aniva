package com.aniva.modules.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aniva.modules.analytics.entity.UserEvent;
import com.aniva.modules.analytics.repository.UserEventRepository;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventRepository repository;

    // 🔥 Lightweight tracking (sync for now)
    public void trackEvent(Long userId,
                           String eventType,
                           String entityType,
                           String entityId,
                           String device,
                           String ip) {

        UserEvent event = UserEvent.builder()
                .userId(userId)
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .deviceInfo(device)
                .ipAddress(ip)
                .build();

        repository.save(event);
    }
}