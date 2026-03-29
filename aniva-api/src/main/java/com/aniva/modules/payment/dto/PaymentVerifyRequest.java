package com.aniva.modules.payment.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {

    private Long orderId;
    private String razorpayOrderId;
    private String paymentId;
    private String signature;
}