package com.aniva.modules.order.service;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.auth.repository.UserRepository;
import com.aniva.modules.cart.dto.CartItemResponse;
import com.aniva.modules.cart.service.CartService;
import com.aniva.modules.inventory.service.InventoryService;
import com.aniva.modules.order.dto.CheckoutRequest;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.dto.OrderStatusResponse;
import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.OrderItemRepository;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.payment.enums.PaymentStatus;
import com.aniva.modules.shipping.service.DelhiveryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final UserOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final DelhiveryService delhiveryService;

    @Override
    @Transactional
    public UserOrder createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItemResponse> cartItems = cartService.getCart(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        UserOrder order = UserOrder.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.CREATED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        for (CartItemResponse cartItem : cartItems) {
            BigDecimal price = BigDecimal.valueOf(cartItem.getPrice());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .totalPrice(itemTotal)
                    .build();

            orderItemRepository.save(orderItem);

            inventoryService.reserveStock(
                    cartItem.getProductId(),
                    cartItem.getQuantity(),
                    order.getId(),
                    "CHECKOUT"
            );
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        cartService.clearCart(userId);

        return order;
    }

    @Override
    @Transactional
    public UserOrder checkout(Long userId) {
        return createOrderFromCart(userId);
    }

    @Override
    @Transactional
    public UserOrder checkout(Long userId, CheckoutRequest request) {
        validateCheckoutRequest(request);
        return createOrderFromCart(userId);
    }

    @Override
    public OrderResponse toResponse(UserOrder order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .paymentOrderId(order.getPaymentOrderId())
                .paymentId(order.getPaymentId())
                .createdAt(order.getCreatedAt())
                .build();
    }

    @Override
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository
                .findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::toResponse);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public UserOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public OrderStatusResponse getOrderStatus(Long userId, Long orderId) {
        UserOrder order = getOrderById(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return OrderStatusResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus() == null ? null : order.getPaymentStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private String generateOrderNumber() {
        return "ANIVA-" + System.currentTimeMillis();
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        if (request == null) {
            return;
        }

        if (request.getShippingAddressId() != null
                && !request.getShippingAddressId().isBlank()
                && !request.getShippingAddressId().chars().allMatch(Character::isDigit)) {
            throw new RuntimeException("Invalid shipping address ID");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return;
        }

        for (var item : request.getItems()) {
            if (item == null || item.getProductId() == null) {
                throw new RuntimeException("Invalid checkout items");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Invalid checkout quantity");
            }
        }
    }

    @Override
    @Transactional
    public UserOrder updateOrderStatus(Long orderId, OrderStatus status) {

        UserOrder order = getOrderById(orderId);

        OrderStatus current = order.getStatus();

        // 🔥 STRICT FLOW CONTROL
        if (current == OrderStatus.CREATED && status != OrderStatus.PAID && status != OrderStatus.CANCELLED) {
            throw new RuntimeException("Invalid transition");
        }

        if (current == OrderStatus.PAID && status != OrderStatus.PROCESSING) {
            throw new RuntimeException("Invalid transition");
        }

        if (current == OrderStatus.PROCESSING && status != OrderStatus.SHIPPED) {
            throw new RuntimeException("Invalid transition");
        }

        if (current == OrderStatus.SHIPPED && status != OrderStatus.DELIVERED) {
            throw new RuntimeException("Invalid transition");
        }

        // ✅ DELHIVERY TEST MODE (AUTO TRACKING)
        if (status == OrderStatus.SHIPPED) {
            order.setPaymentReference("TEST_TRACK_" + order.getId()); // temporary tracking
        }

        order.setStatus(status);

        return orderRepository.save(order);
    }
}
