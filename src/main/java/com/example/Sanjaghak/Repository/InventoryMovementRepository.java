package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.MovementType;
import com.example.Sanjaghak.model.InventoryMovement;
import com.example.Sanjaghak.model.ProductVariants;
import com.example.Sanjaghak.model.Products;
import com.example.Sanjaghak.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID>, JpaSpecificationExecutor<InventoryMovement> {
    long countByMovementTypeAndRefrenceIdAndVariantsId(
            MovementType movementType,
            UUID refrenceId,
            ProductVariants variantsId
    );

    List<InventoryMovement> findByFromWarehouseIdAndMovementType(
            Warehouse fromWarehouseId,
            MovementType movementType
    );

    List<InventoryMovement> findByToWarehouseIdAndMovementType(
            Warehouse fromWarehouseId,
            MovementType movementType
    );

    List<InventoryMovement> findByRefrenceId(UUID refrenceId);

    List<InventoryMovement> findByRefrenceIdAndMovementType(UUID referenceId, MovementType movementType);

    @Query("SELECT im.variantsId.productId " +
            "FROM InventoryMovement im " +
            "WHERE im.movementType = com.example.Sanjaghak.Enum.MovementType.SALE_OUT " +
            "AND im.variantsId.productId.active = true " +
            "GROUP BY im.variantsId.productId " +
            "ORDER BY SUM(im.quantity) DESC")
    List<Products> findTopSellingActiveProducts();

}
