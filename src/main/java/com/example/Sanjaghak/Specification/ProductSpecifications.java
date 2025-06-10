package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.Products;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecifications {
    public static Specification<Products> filterProducts(String productName,
                                                         BigDecimal minPrice,
                                                         BigDecimal maxPrice,
                                                         Boolean active,
                                                         UUID categoryId,
                                                         UUID brandId ){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productName != null && !productName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("productName")),
                        "%" + productName.toLowerCase() + "%"));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("categories").get("id"), categoryId));
            }

            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("brands").get("id"), brandId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
