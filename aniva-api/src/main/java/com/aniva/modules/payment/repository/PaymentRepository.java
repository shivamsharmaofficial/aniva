package com.aniva.modules.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aniva.modules.payment.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {
}