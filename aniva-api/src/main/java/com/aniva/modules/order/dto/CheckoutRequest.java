package com.aniva.modules.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {

    private List<OrderItemRequest> items;

    private String shippingAddressId;

}