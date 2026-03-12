package com.aniva.modules.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;
    private String paymentId;

}