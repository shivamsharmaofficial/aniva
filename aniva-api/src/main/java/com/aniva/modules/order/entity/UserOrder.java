package com.aniva.modules.order.entity;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.auth.entity.User;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.payment.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        schema = "\"order\"",
        name = "orders",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_orders_payment_id", columnNames = "payment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(name = "payment_order_id")
    private String paymentOrderId;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "inventory_lock_id")
    private Long inventoryLockId;

    @Column(name = "order_uuid", unique = true)
    private UUID orderUuid;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> items;

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (status == null)
            status = OrderStatus.CREATED;

        if (paymentStatus == null)
        paymentStatus = PaymentStatus.PENDING;
    }
}
