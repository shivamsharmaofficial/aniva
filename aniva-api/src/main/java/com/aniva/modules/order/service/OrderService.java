package com.aniva.modules.order.service;

import com.aniva.modules.order.dto.CheckoutRequest;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.dto.OrderStatusResponse;
import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    UserOrder checkout(Long userId);

    UserOrder checkout(Long userId, CheckoutRequest request);

    OrderResponse toResponse(UserOrder order);

    Page<OrderResponse> getUserOrders(Long userId, Pageable pageable);

    List<OrderItem> getOrderItems(Long orderId);

    UserOrder getOrderById(Long orderId);

    OrderStatusResponse getOrderStatus(Long userId, Long orderId);

}
