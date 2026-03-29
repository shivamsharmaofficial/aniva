package com.aniva.modules.inventory.service;

import org.springframework.scheduling.annotation.Async;

public interface EventPublisher {

    @Async
    void publish(String eventType, Long productId, Integer quantity, Long referenceId, String referenceType);
}
