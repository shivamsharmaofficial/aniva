package com.aniva.modules.payment.entity;

import com.aniva.core.audit.BaseEntity;
import com.aniva.modules.order.entity.UserOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", schema = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id")
    private UserOrder order;

    private String gateway;

    private String transactionId;

    private String status;

}