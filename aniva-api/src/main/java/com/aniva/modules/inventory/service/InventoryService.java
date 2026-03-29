package com.aniva.modules.inventory.service;

import com.aniva.modules.inventory.entity.Inventory;
import com.aniva.modules.inventory.entity.InventoryLog;
import com.aniva.modules.inventory.entity.InventoryLogChangeType;
import com.aniva.modules.inventory.repository.InventoryRepository;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private static final String STOCK_LOCK_PREFIX = "lock:stock:";

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryCacheService inventoryCacheService;
    private final RedisLockService redisLockService;
    private final EventPublisher eventPublisher;
    private final EntityManager entityManager;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            InventoryCacheService inventoryCacheService,
            RedisLockService redisLockService,
            EventPublisher eventPublisher,
            EntityManager entityManager) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryCacheService = inventoryCacheService;
        this.redisLockService = redisLockService;
        this.eventPublisher = eventPublisher;
        this.entityManager = entityManager;
    }

    public void reserveStock(Long productId, Integer quantity) {
        reserveStock(productId, quantity, null, null);
    }

    @Retryable(maxAttempts = 3)
    public void reserveStock(Long productId, Integer quantity, Long referenceId, String referenceType) {

        validateQuantity(quantity);
        validateProduct(productId);

        String lockKey = STOCK_LOCK_PREFIX + productId;
        String lockOwner = acquireLockWithRetry(lockKey);
        Inventory inventory;

        try {
            inventory = getOrCreateInventory(productId);

            if (inventory.getAvailableStock() < quantity) {
                throw new IllegalStateException("Not enough stock available");
            }

            inventory.setReservedStock(safe(inventory.getReservedStock()) + quantity);
            inventoryRepository.save(inventory);
        } finally {
            redisLockService.releaseLock(lockKey, lockOwner);
        }

        inventoryCacheService.cacheStock(productId, inventory.getAvailableStock());
        logChange(productId, InventoryLogChangeType.RESERVE, quantity, referenceId, referenceType);
        eventPublisher.publish("INVENTORY_RESERVED", productId, quantity, referenceId, referenceType);
    }

    public void releaseStock(Long productId, Integer quantity) {
        releaseStock(productId, quantity, null, null);
    }

    public void releaseStock(Long productId, Integer quantity, Long referenceId, String referenceType) {

        validateQuantity(quantity);
        validateProduct(productId);

        String lockKey = STOCK_LOCK_PREFIX + productId;
        String lockOwner = acquireLockWithRetry(lockKey);
        Inventory inventory;

        try {
            inventory = getOrCreateInventory(productId);

            if (safe(inventory.getReservedStock()) < quantity) {
                throw new IllegalStateException("Reserved stock cannot be negative");
            }

            inventory.setReservedStock(safe(inventory.getReservedStock()) - quantity);
            inventoryRepository.save(inventory);
        } finally {
            redisLockService.releaseLock(lockKey, lockOwner);
        }

        inventoryCacheService.cacheStock(productId, inventory.getAvailableStock());
        logChange(productId, InventoryLogChangeType.RELEASE, quantity, referenceId, referenceType);
    }

    public void confirmStock(Long productId, Integer quantity) {
        confirmStock(productId, quantity, null, null);
    }

    @Retryable(maxAttempts = 3)
    public void confirmStock(Long productId, Integer quantity, Long referenceId, String referenceType) {

        validateQuantity(quantity);
        validateProduct(productId);

        String lockKey = STOCK_LOCK_PREFIX + productId;
        String lockOwner = acquireLockWithRetry(lockKey);
        Inventory inventory;

        try {
            inventory = getOrCreateInventory(productId);

            if (safe(inventory.getReservedStock()) < quantity) {
                throw new IllegalStateException("Reserved stock cannot be negative");
            }

            if (safe(inventory.getTotalStock()) < quantity) {
                throw new IllegalStateException("Total stock cannot be negative");
            }

            inventory.setReservedStock(safe(inventory.getReservedStock()) - quantity);
            inventory.setTotalStock(safe(inventory.getTotalStock()) - quantity);
            inventoryRepository.save(inventory);
        } finally {
            redisLockService.releaseLock(lockKey, lockOwner);
        }

        inventoryCacheService.cacheStock(productId, inventory.getAvailableStock());
        logChange(productId, InventoryLogChangeType.CONFIRM, quantity, referenceId, referenceType);
        eventPublisher.publish("INVENTORY_CONFIRMED", productId, quantity, referenceId, referenceType);
    }

    public void addStock(Long productId, Integer quantity) {
        addStock(productId, quantity, null, null);
    }

    public void addStock(Long productId, Integer quantity, Long referenceId, String referenceType) {

        validateQuantity(quantity);
        validateProduct(productId);
        Inventory inventory = getOrCreateInventory(productId);

        inventory.setTotalStock(safe(inventory.getTotalStock()) + quantity);
        inventoryRepository.save(inventory);
        inventoryCacheService.cacheStock(productId, inventory.getAvailableStock());
        logChange(productId, InventoryLogChangeType.ADD, quantity, referenceId, referenceType);
    }

    public Integer getAvailableStock(Long productId) {

        Integer cachedStock = inventoryCacheService.getStock(productId);
        if (cachedStock != null) {
            return cachedStock;
        }

        String lockKey = STOCK_LOCK_PREFIX + productId;
        String lockOwner = acquireLockWithRetry(lockKey);

        try {
            Integer recheckedStock = inventoryCacheService.getStock(productId);
            if (recheckedStock != null) {
                return recheckedStock;
            }

            validateProduct(productId);
            Inventory inventory = getOrCreateInventoryForRead(productId);
            Integer availableStock = inventory.getAvailableStock();
            inventoryCacheService.cacheStock(productId, availableStock);

            return availableStock;
        } finally {
            redisLockService.releaseLock(lockKey, lockOwner);
        }
    }

    private Inventory getOrCreateInventory(Long productId) {

        return inventoryRepository.findByProductIdForUpdate(productId)
                .orElseGet(() -> createInventory(productId));
    }

    private Inventory getOrCreateInventoryForRead(Long productId) {

        return inventoryRepository.findByProductId(productId)
                .orElseGet(() -> createInventory(productId));
    }

    private Inventory createInventory(Long productId) {

        try {
            return inventoryRepository.saveAndFlush(
                    Inventory.builder()
                            .productId(productId)
                            .totalStock(0)
                            .reservedStock(0)
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            return inventoryRepository.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> ex);
        }
    }

    private void validateProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (Boolean.TRUE.equals(product.getIsDeleted()) || !Boolean.TRUE.equals(product.getIsActive())) {
            throw new IllegalStateException("Product not available");
        }
    }

    private void logChange(
            Long productId,
            InventoryLogChangeType changeType,
            Integer quantity,
            Long referenceId,
            String referenceType) {

        try {
            entityManager.persist(
                    InventoryLog.builder()
                            .productId(productId)
                            .changeType(changeType)
                            .quantity(quantity)
                            .referenceId(referenceId)
                            .referenceType(referenceType)
                            .build()
            );
        } catch (Exception ignored) {
        }
    }

    private String acquireLockWithRetry(String lockKey) {

        for (int attempt = 0; attempt < 3; attempt++) {
            String lockOwner = redisLockService.acquireLock(lockKey);
            if (lockOwner != null) {
                return lockOwner;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while acquiring stock lock", ex);
            }
        }

        throw new IllegalStateException("Unable to acquire stock lock");
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
