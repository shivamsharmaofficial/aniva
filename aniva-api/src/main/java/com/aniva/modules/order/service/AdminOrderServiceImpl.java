package com.aniva.modules.order.service;

import com.aniva.modules.order.dto.UpdateOrderStatusRequest;
import com.aniva.modules.order.entity.OrderShipment;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.OrderShipmentRepository;
import com.aniva.modules.order.repository.UserOrderRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final UserOrderRepository orderRepository;
    private final OrderShipmentRepository orderShipmentRepository;

    @Override
    public Page<UserOrder> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public UserOrder getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public UserOrder updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());
        return orderRepository.save(order);
    }

    @Override
    public UserOrder shipOrder(Long orderId, String trackingNumber) {

        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.SHIPPED);

        OrderShipment shipment = OrderShipment.builder()
                .order(order)
                .carrier("DELHIVERY")
                .trackingNumber(trackingNumber)
                .status("SHIPPED")
                .shippedAt(LocalDateTime.now())
                .build();

        orderShipmentRepository.save(shipment);

        return orderRepository.save(order);
    }

    @Override
    public UserOrder updateTracking(Long orderId, String trackingNumber) {

        OrderShipment shipment = orderShipmentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setTrackingNumber(trackingNumber);

        orderShipmentRepository.save(shipment);

        return shipment.getOrder();
    }

    @Override
    public UserOrder markDelivered(Long orderId) {

        OrderShipment shipment = orderShipmentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus("DELIVERED");
        shipment.setDeliveredAt(LocalDateTime.now());

        orderShipmentRepository.save(shipment);

        UserOrder order = shipment.getOrder();
        order.setStatus(OrderStatus.DELIVERED);

        return orderRepository.save(order);
    }
}