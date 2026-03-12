package com.aniva.modules.payment.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.OrderItemRepository;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.payment.enums.PaymentStatus;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.repository.ProductRepository;

import com.aniva.modules.system.entity.SystemSetting;
import com.aniva.modules.system.repository.SystemSettingRepository;
import com.aniva.modules.system.service.EmailService;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SystemSettingRepository settingRepository;
    private final EmailService emailService;

    private RazorpayClient razorpayClient;

    /* ========================
       GET PAYMENT MODE
    ======================== */

    public String getPaymentMode() {

        return settingRepository
                .findByKey("PAYMENT_MODE")
                .map(SystemSetting::getValue)
                .orElse("MOCK");
    }

    /* ========================
       CREATE PAYMENT ORDER
    ======================== */

    public String createPayment(Long orderId) {

        try {

            UserOrder order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            JSONObject options = new JSONObject();

            options.put(
                    "amount",
                    order.getTotalAmount().multiply(new BigDecimal(100))
            );

            options.put("currency", "INR");

            options.put("receipt", order.getOrderNumber());

            Order razorOrder = razorpayClient.orders.create(options);

            return razorOrder.toString();

        } catch (Exception e) {

            throw new RuntimeException("Razorpay order creation failed", e);
        }
    }

    /* ========================
       CONFIRM PAYMENT
    ======================== */

    @Transactional
    public UserOrder confirmPayment(Long orderId, String paymentId) {

        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        /* ========================
           UPDATE ORDER STATUS
        ======================== */

        order.setPaymentId(paymentId);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);

        /* ========================
           UPDATE INVENTORY
        ======================== */

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {

            Product product = productRepository
                    .findByIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int reserved = product.getReservedStock();

            if (reserved < item.getQuantity()) {

                throw new RuntimeException(
                        "Reserved stock mismatch for product: "
                                + product.getName()
                );
            }

            /* ========================
               FINALIZE STOCK
            ======================== */

            product.setReservedStock(
                    reserved - item.getQuantity()
            );

            product.setTotalStock(
                    product.getTotalStock() - item.getQuantity()
            );

            productRepository.save(product);
        }

        /* ========================
           SEND EMAIL
        ======================== */

        emailService.sendOrderConfirmation(
                order.getUser().getEmail(),
                order.getOrderNumber()
        );

        return orderRepository.save(order);
    }
}