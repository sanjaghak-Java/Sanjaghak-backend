package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Sections;
import com.example.Sanjaghak.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, UUID>, JpaSpecificationExecutor<Sections> {
    boolean existsByNameAndWarehouseId(String name, Warehouse warehouse);
    List<Sections> findByWarehouseId_WarehouseId(UUID warehouseId);
    List<Sections> findByWarehouseId(Warehouse warehouse);
}
