package com.aniva.modules.order.repository;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.order.entity.UserOrder;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    @Query("""
    SELECT o FROM UserOrder o
    WHERE o.status = 'PENDING'
    AND o.createdAt < :time
    """)
    List<UserOrder> findPendingOrdersOlderThan(LocalDateTime time);

    Page<UserOrder> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}