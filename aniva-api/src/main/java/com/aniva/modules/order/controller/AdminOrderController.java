package com.aniva.modules.order.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.dto.UpdateOrderStatusRequest;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.service.AdminOrderService;
import com.aniva.modules.order.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Page<OrderResponse>> getAllOrders(Pageable pageable) {

        Page<OrderResponse> response =
                adminOrderService.getAllOrders(pageable)
                        .map(orderService::toResponse);

        return ApiResponse.success("Orders fetched successfully", response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {

        UserOrder order = adminOrderService.getOrder(orderId);

        return ApiResponse.success(
                "Order fetched",
                orderService.toResponse(order)
        );
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        UserOrder order =
                adminOrderService.updateOrderStatus(orderId, request);

        return ApiResponse.success(
                "Order status updated",
                orderService.toResponse(order)
        );
    }

    @PatchMapping("/{id}/ship")
    public ApiResponse<OrderResponse> shipOrder(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {

        UserOrder order =
                adminOrderService.shipOrder(id, trackingNumber);

        return ApiResponse.success(
                "Order shipped",
                orderService.toResponse(order)
        );
    }

    @PatchMapping("/{id}/tracking")
    public ApiResponse<OrderResponse> updateTracking(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {

        UserOrder order =
                adminOrderService.updateTracking(id, trackingNumber);

        return ApiResponse.success(
                "Tracking updated",
                orderService.toResponse(order)
        );
    }

    @PatchMapping("/{id}/deliver")
    public ApiResponse<OrderResponse> markDelivered(
            @PathVariable Long id) {

        UserOrder order =
                adminOrderService.markDelivered(id);

        return ApiResponse.success(
                "Order delivered",
                orderService.toResponse(order)
        );
    }
}