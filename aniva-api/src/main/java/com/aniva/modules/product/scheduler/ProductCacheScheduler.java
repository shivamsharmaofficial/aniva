package com.aniva.modules.product.scheduler;

import com.aniva.modules.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ProductCacheScheduler {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheScheduler.class);

    private final AtomicBoolean warming = new AtomicBoolean(false);
    private final ProductService productService;

    public ProductCacheScheduler(ProductService productService) {
        this.productService = productService;
    }

    @Scheduled(fixedRate = 300000)
    public void warmProductCache() {
        if (!warming.compareAndSet(false, true)) {
            log.debug("product_cache_warmup_skipped reason=already_running");
            return;
        }

        try {
            productService.getProducts(
                    null,
                    null,
                    null,
                    null,
                    "ACTIVE",
                    false,
                    "createdAt",
                    "desc",
                    0,
                    10
            );
        } catch (Exception ex) {
            log.warn("product_cache_warmup_failed message={}", ex.getMessage(), ex);
        } finally {
            warming.set(false);
        }
    }
}
