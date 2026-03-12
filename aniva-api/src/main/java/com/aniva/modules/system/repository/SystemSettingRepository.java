package com.aniva.modules.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aniva.modules.system.entity.SystemSetting;

public interface SystemSettingRepository
        extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findByKey(String key);

}