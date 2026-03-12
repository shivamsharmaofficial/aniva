package com.aniva.modules.order.service;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.auth.repository.UserRepository;
import com.aniva.modules.cart.entity.Cart;
import com.aniva.modules.cart.entity.CartItem;
import com.aniva.modules.cart.repository.CartRepository;
import com.aniva.modules.cart.repository.CartItemRepository;
import com.aniva.modules.order.dto.OrderResponse;
import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.OrderItemRepository;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.payment.enums.PaymentStatus;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final UserOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /* ========================
       CHECKOUT
    ======================== */

    @Override
    @Transactional
    public UserOrder checkout(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Product product = productRepository
                    .findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int availableStock =
                    product.getTotalStock() - product.getReservedStock();

            if (availableStock < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Not enough stock for " + product.getName());
            }

            product.setReservedStock(
                    product.getReservedStock() + cartItem.getQuantity());

            productRepository.save(product);

            BigDecimal price =
                    product.getDiscountPrice() != null
                            ? product.getDiscountPrice()
                            : product.getPrice();

            BigDecimal itemTotal =
                    price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(cartItem.getQuantity())
                    .price(price)
                    .totalPrice(itemTotal)
                    .build();

            items.add(orderItem);
        }

        UserOrder order = UserOrder.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        for (OrderItem item : items) {
            item.setOrder(order);
        }

        orderItemRepository.saveAll(items);

        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    /* ========================
       ENTITY → DTO
    ======================== */

    @Override
    public OrderResponse toResponse(UserOrder order) {

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }

    /* ========================
       USER ORDERS
    ======================== */

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

    private String generateOrderNumber() {
        return "ANIVA-" + System.currentTimeMillis();
    }
}