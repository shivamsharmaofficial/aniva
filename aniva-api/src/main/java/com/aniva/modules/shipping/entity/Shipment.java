package com.aniva.modules.shipping.entity;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.shipping.enums.ShipmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "shipments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_shipments_tracking_number", columnNames = "tracking_number")
        },
        indexes = {
                @Index(name = "idx_shipments_order_id", columnList = "order_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;
}
