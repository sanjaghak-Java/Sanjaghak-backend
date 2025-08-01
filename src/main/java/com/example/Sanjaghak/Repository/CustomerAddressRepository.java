package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Customer;
import com.example.Sanjaghak.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID>, JpaSpecificationExecutor<CustomerAddress> {
    Optional<CustomerAddress> findByCustomerId(Customer customerId);
}
