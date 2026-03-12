package com.aniva.modules.order.dto;

import com.aniva.modules.order.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    private OrderStatus status;

}