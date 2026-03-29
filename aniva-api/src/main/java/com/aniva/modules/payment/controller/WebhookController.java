package com.aniva.modules.payment.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.service.OrderService;
import com.aniva.modules.payment.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public WebhookController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/razorpay")
    public ApiResponse<OrderResponse> handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) {

        try {
            UserOrder order = paymentService.handleCapturedPaymentWebhook(payload, signature);

            if (order == null) {
                return ApiResponse.success("Webhook ignored", null);
            }

            return ApiResponse.success(
                    "Webhook processed successfully",
                    orderService.toResponse(order)
            );
        } catch (Exception ex) {
            return ApiResponse.failure("Webhook rejected");
        }
    }
}
