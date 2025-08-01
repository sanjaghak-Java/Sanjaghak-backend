package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.InventoryStock;
import com.example.Sanjaghak.model.ProductVariants;
import com.example.Sanjaghak.model.Shelves;
import com.example.Sanjaghak.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID>, JpaSpecificationExecutor<InventoryStock> {

    boolean existsByVariantsIdAndShelvesId(ProductVariants variantsId, Shelves shelvesId);

    boolean existsByShelvesId_ShelvesId(UUID shelvesId);

    List<InventoryStock> findByShelvesIdIn(List<Shelves> shelves);

    List<InventoryStock> findByShelvesIdInAndVariantsId(List<Shelves> shelves, ProductVariants variantsId);

    Optional<InventoryStock> findInventoryStockByShelvesIdAndVariantsId(Shelves shelves, ProductVariants variantsId);

    @Query("SELECT SUM(i.quantityOnHand) FROM InventoryStock i WHERE i.variantsId.id = :variantId AND i.isActive = true")
    Integer getTotalStockByVariantId(@Param("variantId") UUID variantId);

    // موجودی فعال در یک انبار خاص
    List<InventoryStock> findByVariantsId_VariantIdAndShelvesId_SectionsId_WarehouseIdAndIsActive(
            UUID variantId,
            Warehouse warehouse,
            Boolean isActive
    );

    // موجودی فعال در انبارهای غیر مرکزی
    @Query("SELECT i FROM InventoryStock i " +
            "WHERE i.variantsId.variantId = :variantId " +
            "AND i.shelvesId.sectionsId.warehouseId.isActive = true " +
            "AND i.shelvesId.sectionsId.warehouseId.isCentral = false " +
            "AND i.isActive = true")
    List<InventoryStock> findByVariantsId_VariantIdAndShelvesId_SectionsId_WarehouseIdIsActiveAndNotCentral(
            @Param("variantId") UUID variantId
    );

    Optional<InventoryStock> findByVariantsIdAndShelvesId(ProductVariants variantsId, Shelves shelvesId);

    Optional<InventoryStock> findByShelvesIdAndVariantsId(Shelves shelves, ProductVariants variants);

    Optional<InventoryStock> findByVariantsIdAndShelvesIdAndIsActiveTrue(ProductVariants variants, Shelves shelves);

    List<InventoryStock> findByShelvesId_IsReturnTrue();
}
