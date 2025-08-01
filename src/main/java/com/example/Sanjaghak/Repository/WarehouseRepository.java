package com.example.Sanjaghak.Repository;


import com.example.Sanjaghak.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID>, JpaSpecificationExecutor<Warehouse> {
    boolean existsByIsCentralTrue();
    boolean existsByPhone(String phone);
    boolean existsByPostalCode(String postalCode);
    Optional<Warehouse> findByIsCentralTrueAndIsActiveTrue();
    Optional<Warehouse> findByIsCentral(boolean IsCentral);
    Optional<Warehouse> findByIsCentralAndIsActive(boolean isCentral, boolean isActive);

}

