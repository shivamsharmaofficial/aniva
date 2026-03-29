package com.aniva.modules.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

import java.util.Optional;

import com.aniva.modules.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"images", "category"})
    Optional<Product> findBySlugAndIsDeletedFalse(String slug);

    @EntityGraph(attributePaths = {"images", "category"})
    Optional<Product> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"images", "category"})
    Page<Product> findByIsDeletedFalseAndIsActiveTrue(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"images", "category"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    /* ===============================
       LOCK PRODUCT ROW FOR CHECKOUT
       Prevent overselling
    =============================== */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}
