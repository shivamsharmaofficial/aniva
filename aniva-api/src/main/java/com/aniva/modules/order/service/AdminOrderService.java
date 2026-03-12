package com.aniva.modules.order.service;

import com.aniva.modules.order.dto.UpdateOrderStatusRequest;
import com.aniva.modules.order.entity.UserOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminOrderService {

    Page<UserOrder> getAllOrders(Pageable pageable);

    UserOrder getOrder(Long orderId);

    UserOrder updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    UserOrder shipOrder(Long orderId, String trackingNumber);

    UserOrder updateTracking(Long orderId, String trackingNumber);

    UserOrder markDelivered(Long orderId);

}