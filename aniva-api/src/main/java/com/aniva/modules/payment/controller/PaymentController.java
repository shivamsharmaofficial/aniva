package com.aniva.modules.payment.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.payment.dto.PaymentRequest;
import com.aniva.modules.payment.service.PaymentService;
import com.aniva.modules.system.entity.SystemSetting;
import com.aniva.modules.system.repository.SystemSettingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SystemSettingRepository settingRepository;

    /* ========================
       GET PAYMENT MODE
    ======================== */

    @GetMapping("/mode")
    public ApiResponse<String> getMode() {

        return ApiResponse.success(
                "Payment mode fetched",
                paymentService.getPaymentMode()
        );
    }

    /* ========================
       ADMIN CHANGE MODE
    ======================== */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/mode")
    public ApiResponse<Void> changeMode(@RequestParam String mode) {

        SystemSetting setting = settingRepository
                .findByKey("PAYMENT_MODE")
                .orElseThrow(() -> new RuntimeException("Payment mode setting not found"));

        setting.setValue(mode);

        settingRepository.save(setting);

        return ApiResponse.success(
                "Payment mode updated successfully",
                null
        );
    }

    /* ========================
       CREATE PAYMENT ORDER
    ======================== */

    @PostMapping("/create")
    public ApiResponse<String> createPayment(@RequestParam Long orderId) {

        return ApiResponse.success(
                "Payment order created",
                paymentService.createPayment(orderId)
        );
    }

    /* ========================
       CONFIRM PAYMENT
    ======================== */

    @PostMapping("/confirm")
    public ApiResponse<OrderResponse> confirmPayment(
            @RequestBody PaymentRequest request
    ) {

        UserOrder order = paymentService.confirmPayment(
                request.getOrderId(),
                request.getPaymentId()
        );

        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();

        return ApiResponse.success(
                "Payment confirmed successfully",
                response
        );
    }
}