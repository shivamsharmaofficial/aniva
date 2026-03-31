package com.aniva.modules.order.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.core.security.CustomUserDetails;
import com.aniva.modules.order.dto.*;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.service.OrderService;
import com.aniva.modules.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    /* ========================
       CHECKOUT
    ======================== */

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @RequestBody(required = false) CheckoutRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Step 1: Create Order
        UserOrder order = orderService.checkout(user.getUserId(), request);

        // Step 2: Create Payment
        String paymentData = paymentService.createPayment(order.getId());

        // Step 3: Fetch UPDATED Order
        UserOrder updatedOrder = orderService.getOrderById(order.getId());

        // Step 4: Build Response
        CheckoutResponse response = CheckoutResponse.builder()
                .order(orderService.toResponse(updatedOrder))
                .paymentData(paymentData)
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Order created successfully", response)
        );
    }

    /* ========================
       MY ORDERS
    ======================== */

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Page<OrderResponse> orders =
                orderService.getUserOrders(user.getUserId(), pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Orders fetched successfully", orders)
        );
    }

    /* ========================
       GET ORDER
    ======================== */

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderId
    ) {

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        OrderStatusResponse status =
                orderService.getOrderStatus(user.getUserId(), orderId);

        OrderResponse response =
                orderService.toResponse(
                        orderService.getOrderById(status.getOrderId())
                );

        return ResponseEntity.ok(
                ApiResponse.success("Order fetched successfully", response)
        );
    }

    /* ========================
       ORDER ITEMS
    ======================== */

    @GetMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderItems(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderId
    ) {

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        orderService.getOrderStatus(user.getUserId(), orderId);

        List<OrderItemResponse> response = orderService.getOrderItems(orderId)
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Order items fetched", response)
        );
    }

    /* ========================
       ORDER STATUS
    ======================== */

    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getOrderStatus(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderId
    ) {

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order status fetched successfully",
                        orderService.getOrderStatus(user.getUserId(), orderId)
                )
        );
    }
}