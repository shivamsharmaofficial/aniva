package com.aniva.modules.shipping.repository;

import com.aniva.modules.shipping.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    List<ShippingAddress> findByUserIdOrderByCreatedAtDesc(Long userId);
}
