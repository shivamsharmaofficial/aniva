package com.aniva.modules.order.repository;

import com.aniva.modules.order.entity.OrderShipment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderShipmentRepository extends JpaRepository<OrderShipment, Long> {

    Optional<OrderShipment> findByOrderId(Long orderId);
}