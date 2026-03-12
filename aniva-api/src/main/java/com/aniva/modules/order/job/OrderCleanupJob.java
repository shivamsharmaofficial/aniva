package com.aniva.modules.order.job;

import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.product.repository.ProductRepository;
import com.aniva.modules.product.entity.Product;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCleanupJob {

    private final UserOrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void releaseExpiredOrders() {

        List<UserOrder> pendingOrders =
                orderRepository.findPendingOrdersOlderThan(
                        LocalDateTime.now().minusMinutes(15)
                );

        for (UserOrder order : pendingOrders) {

        order.setStatus(OrderStatus.CANCELLED);

            List<OrderItem> items = order.getItems();

            for (OrderItem item : items) {

                Product product = productRepository
                        .findById(item.getProductId())
                        .orElseThrow();

                product.setReservedStock(
                        product.getReservedStock() - item.getQuantity()
                );

                productRepository.save(product);
            }

            orderRepository.save(order);
        }
    }
}