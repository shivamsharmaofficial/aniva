package com.aniva.modules.order.entity;

import java.time.LocalDateTime;

import com.aniva.core.audit.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "\"order\"", name = "order_shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShipment extends BaseEntity {

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private UserOrder order;
}