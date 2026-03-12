package com.aniva.modules.order.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.core.security.CustomUserDetails;
import com.aniva.modules.order.dto.CheckoutRequest;
import com.aniva.modules.order.dto.OrderItemResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* ========================
       CHECKOUT
    ======================== */

    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) CheckoutRequest request
    ) {

        UserOrder order = orderService.checkout(userDetails.getUserId());

        OrderResponse response = orderService.toResponse(order);

        return ApiResponse.success(
                "Order created successfully",
                response
        );
    }

    /* ========================
       MY ORDERS
    ======================== */

    @GetMapping("/my-orders")
    public ApiResponse<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {

        Page<OrderResponse> orders =
                orderService.getUserOrders(userDetails.getUserId(), pageable);

        return ApiResponse.success(
                "Orders fetched successfully",
                orders
        );
    }

    /* ========================
       GET ORDER
    ======================== */

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ) {

        UserOrder order = orderService.getOrderById(orderId);

        if (!order.getUser().getId().equals(userDetails.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        OrderResponse response = orderService.toResponse(order);

        return ApiResponse.success(
                "Order fetched successfully",
                response
        );
    }

    /* ========================
       ORDER ITEMS
    ======================== */

    @GetMapping("/{orderId}/items")
    public ApiResponse<List<OrderItemResponse>> getOrderItems(
            @PathVariable Long orderId
    ) {

        List<OrderItem> items = orderService.getOrderItems(orderId);

        List<OrderItemResponse> response = items.stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return ApiResponse.success(
                "Order items fetched",
                response
        );
    }
}