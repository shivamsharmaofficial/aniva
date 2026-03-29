package com.aniva.modules.inventory.entity;

import com.aniva.core.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory", schema = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "total_stock", nullable = false)
    private Integer totalStock;

    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock;

    @Transient
    public Integer getAvailableStock() {
        return safe(totalStock) - safe(reservedStock);
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
