package com.aniva.modules.inventory.service;

import com.aniva.modules.inventory.entity.OutboxEvent;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxProcessorService {

    private static final int MAX_RETRY_COUNT = 5;

    private final EntityManager entityManager;

    public OutboxProcessorService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processPendingEvents() {

        List<OutboxEvent> events = entityManager.createQuery(
                        "SELECT e FROM OutboxEvent e WHERE e.status IN :statuses ORDER BY e.createdAt ASC",
                        OutboxEvent.class
                )
                .setParameter("statuses", List.of("PENDING", "FAILED"))
                .setMaxResults(100)
                .getResultList();

        for (OutboxEvent event : events) {
            try {
                processEvent(event);
                event.setStatus("PROCESSED");
            } catch (Exception ex) {
                int retryCount = safeRetryCount(event.getRetryCount()) + 1;
                event.setRetryCount(retryCount);
                event.setStatus(retryCount >= MAX_RETRY_COUNT ? "DEAD" : "FAILED");
            }

            entityManager.merge(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        event.getEventType();
        event.getPayload();
    }

    private int safeRetryCount(Integer retryCount) {
        return retryCount == null ? 0 : retryCount;
    }
}
