package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.OrderItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderItemSpecifications {

    public static Specification<OrderItem> filterOrderItems(BigDecimal minTotalAmount,
                                                        BigDecimal maxTotalAmount,
                                                        UUID orderId,
                                                        UUID productId ){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minTotalAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), minTotalAmount));
            }

            if (maxTotalAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), maxTotalAmount));
            }

            if (orderId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("orderId").get("orderId"), orderId));
            }

            if (productId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("productId").get("productId"), productId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
