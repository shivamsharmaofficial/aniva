package com.aniva.modules.shipping.service;

import com.aniva.modules.order.entity.UserOrder;
import org.springframework.stereotype.Service;

@Service
public class DelhiveryService {

    public String createShipment(UserOrder order) {

        // 🔥 TEST MODE (for now)
        return "DLV_TEST_" + order.getId();
    }
}