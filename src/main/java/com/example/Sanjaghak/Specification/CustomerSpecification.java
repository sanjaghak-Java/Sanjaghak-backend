package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerSpecification {

    public static Specification<Customer> filterCustomer(UUID userId){

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userId").get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
