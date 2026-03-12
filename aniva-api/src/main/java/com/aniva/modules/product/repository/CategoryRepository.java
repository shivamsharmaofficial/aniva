package com.aniva.modules.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aniva.modules.product.entity.Category;

import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIsActiveTrueAndIsDeletedFalse();
}