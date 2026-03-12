package com.aniva.modules.system.entity;

import jakarta.persistence.Table;

import com.aniva.core.audit.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting extends BaseEntity {

    @Column(name = "setting_key", unique = true)
    private String key;

    @Column(name = "setting_value")
    private String value;

}