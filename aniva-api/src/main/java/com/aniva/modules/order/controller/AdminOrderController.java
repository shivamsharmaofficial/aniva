package com.aniva.modules.order.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.order.dto.OrderItemResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.dto.UpdateOrderStatusRequest;
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
@PreAuthorize("hasRole('ADMIN')")
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

        return ApiResponse.success(
                "Order fetched",
                orderService.toResponse(adminOrderService.getOrder(orderId))
        );
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        return ApiResponse.success(
                "Order status updated",
                orderService.toResponse(adminOrderService.updateOrderStatus(orderId, request))
        );
    }

    @GetMapping("/{orderId}/items")
    public ApiResponse<java.util.List<OrderItemResponse>> getOrderItems(@PathVariable Long orderId) {

        return ApiResponse.success(
                "Order items fetched successfully",
                orderService.getOrderItems(orderId).stream()
                        .map(item -> OrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .totalPrice(item.getTotalPrice())
                                .build())
                        .toList()
        );
    }
}
