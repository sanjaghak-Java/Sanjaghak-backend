package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.ProductAttribute;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AttributeSpecifications {

    public static Specification<ProductAttribute> filterAttribute(String attributeName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (attributeName != null && !attributeName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("attributeName")),
                        "%" + attributeName.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
