package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.Orders;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSpecifications {

    public static Specification<Orders> filterOrders(BigDecimal minTotalAmount,
                                                     BigDecimal maxTotalAmount,
                                                     UUID customerId,
                                                     String orderNumber,
                                                     UUID billingAddressId ){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minTotalAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), minTotalAmount));
            }

            if (maxTotalAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), maxTotalAmount));
            }

            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("customerId").get("customerId"), customerId));
            }

            if (billingAddressId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("billingAddressId").get("addressId"), billingAddressId));
            }

            if (orderNumber != null && !orderNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("orderNumber")),
                        "%" + orderNumber.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
