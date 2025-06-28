package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.Brands;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BrandSpecification {

    public static Specification<Brands> filterBrand(String brandName){
        return (root, query, criteriaBuilder) -> {
                List< Predicate > predicates = new ArrayList<>();
        if(brandName != null && !brandName.isEmpty() ) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("brandName")),
                    "%" + brandName.toLowerCase() + "%"));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
   }
}
