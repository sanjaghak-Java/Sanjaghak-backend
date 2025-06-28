package com.example.Sanjaghak.Specification;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.model.UserAccounts;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserAccounts> filterUser(String name , Boolean active , User_role role){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                String pattern = "%" + name.toLowerCase() + "%";
                Predicate firstNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        pattern);
                Predicate lastNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        pattern);
                predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
            }

            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), active));
            }
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
