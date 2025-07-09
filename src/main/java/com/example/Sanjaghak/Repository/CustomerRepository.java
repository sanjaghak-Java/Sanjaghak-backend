package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByUserId_Id(UUID userId);

    boolean existsByUserId_Id(UUID userId);

}
