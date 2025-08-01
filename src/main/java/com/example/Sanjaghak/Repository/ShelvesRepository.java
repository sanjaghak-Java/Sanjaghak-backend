package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Sections;
import com.example.Sanjaghak.model.Shelves;
import com.example.Sanjaghak.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShelvesRepository extends JpaRepository<Shelves, UUID>, JpaSpecificationExecutor<Shelves> {
    List<Shelves> findBySectionsId(Sections sections);
    List<Shelves> findBySectionsId_SectionsId(UUID sectionsId);
    List<Shelves> findBySectionsIdIn(List<Sections> sections);
    List<Shelves> findBySectionsId_WarehouseIdAndIsReturnTrueAndIsActiveTrue(Warehouse warehouse);
}

