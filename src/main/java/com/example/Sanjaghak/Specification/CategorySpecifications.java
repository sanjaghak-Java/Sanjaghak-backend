package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.Categories;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecifications {

    public static Specification<Categories> filterCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(categoryName != null && !categoryName.isEmpty() ) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("categoryName")),
                        "%" + categoryName.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
