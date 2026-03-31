package com.aniva.modules.payment.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {

    private Long orderId;
    private String razorpayOrderId;

    // ✅ FIXED NAMES
    private String razorpayPaymentId;
    private String razorpaySignature;
}