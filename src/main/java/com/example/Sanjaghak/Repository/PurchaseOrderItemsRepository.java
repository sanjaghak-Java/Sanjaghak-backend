package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.ProductVariants;
import com.example.Sanjaghak.model.PurchaseOrderItems;
import com.example.Sanjaghak.model.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderItemsRepository extends JpaRepository<PurchaseOrderItems, UUID>, JpaSpecificationExecutor<PurchaseOrderItems> {

    boolean existsByPurchaseOrdersIdAndVariantsId(PurchaseOrders purchaseOrdersId, ProductVariants variantsId);

    List<PurchaseOrderItems> findByPurchaseOrdersId_PurchaseOrdersId(UUID purchaseOrdersId);
}
