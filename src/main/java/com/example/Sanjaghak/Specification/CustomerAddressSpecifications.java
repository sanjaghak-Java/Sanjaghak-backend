package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.model.CustomerAddress;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerAddressSpecifications {

    public static Specification<CustomerAddress> filterAddresses(String city,
                                                                 String state,
                                                                 String country,
                                                                 String postalCode,
                                                                 UUID customerId){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")),
                        "%" + city.toLowerCase() + "%"));
            }

            if (state != null && !state.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("state")),
                        "%" + state.toLowerCase() + "%"));
            }


            if (country != null && !country.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("country")),
                        "%" + country.toLowerCase() + "%"));
            }


            if (postalCode != null && !postalCode.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("postalCode")),
                        "%" + postalCode.toLowerCase() + "%"));
            }


            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("customerId").get("id"), customerId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
