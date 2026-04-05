package com.aniva.modules.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aniva.modules.analytics.entity.UserEvent;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
}