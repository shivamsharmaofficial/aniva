package com.aniva.modules.order.repository;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.order.entity.UserOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    @Query("""
    SELECT o FROM UserOrder o
    WHERE o.status = 'PENDING'
    AND o.createdAt < :time
    """)
    List<UserOrder> findPendingOrdersOlderThan(LocalDateTime time);

    @EntityGraph(attributePaths = {"user"})
    Page<UserOrder> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<UserOrder> findById(Long id);

    Optional<UserOrder> findByOrderNumber(String orderNumber);
}
