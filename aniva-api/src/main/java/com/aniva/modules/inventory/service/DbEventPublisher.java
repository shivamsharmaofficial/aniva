package com.aniva.modules.inventory.service;

import com.aniva.modules.inventory.entity.OutboxEvent;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbEventPublisher implements EventPublisher {

    private final EntityManager entityManager;

    public DbEventPublisher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Async
    @Transactional
    public void publish(String eventType, Long productId, Integer quantity, Long referenceId, String referenceType) {

        try {
            JSONObject payload = new JSONObject();
            payload.put("productId", productId);
            payload.put("quantity", quantity);
            payload.put("referenceId", referenceId);
            payload.put("referenceType", referenceType);

            entityManager.persist(
                    OutboxEvent.builder()
                            .retryCount(0)
                            .eventType(eventType)
                            .payload(payload.toString())
                            .status("PENDING")
                            .build()
            );
        } catch (Exception ignored) {
        }
    }
}
