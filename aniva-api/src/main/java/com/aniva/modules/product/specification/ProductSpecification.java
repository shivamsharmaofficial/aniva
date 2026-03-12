package com.aniva.modules.product.specification;

import org.springframework.data.jpa.domain.Specification;

import com.aniva.modules.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            List<String> categorySlugs,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String search,
            String status,
            Boolean includeDeleted
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // 🔹 Soft Delete Filter
            if (includeDeleted == null || !includeDeleted) {
                predicate = cb.and(predicate,
                        cb.isFalse(root.get("isDeleted")));
            }
            // 🔹 Status Filter
            if ("ACTIVE".equalsIgnoreCase(status)) {
                predicate = cb.and(predicate,
                        cb.isTrue(root.get("isActive")));
            }

            if ("INACTIVE".equalsIgnoreCase(status)) {
                predicate = cb.and(predicate,
                        cb.isFalse(root.get("isActive")));
            }

            // 🔹 Category Filter
            if (categorySlugs != null && !categorySlugs.isEmpty()) {
                predicate = cb.and(predicate,
                        root.get("category")
                                .get("slug")
                                .in(categorySlugs));
            }

            // 🔹 Price Filters
            if (minPrice != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(
                                root.get("price"), maxPrice));
            }

            // 🔹 Search
            if (search != null && !search.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + search.toLowerCase() + "%"
                        ));
            }

            return predicate;
        };
    }
}