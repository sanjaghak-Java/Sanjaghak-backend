package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.Statuses;
import com.example.Sanjaghak.model.ProductVariants;
import com.example.Sanjaghak.model.PurchaseOrderItems;
import com.example.Sanjaghak.model.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderItemsRepository extends JpaRepository<PurchaseOrderItems, UUID>, JpaSpecificationExecutor<PurchaseOrderItems> {

    boolean existsByPurchaseOrdersIdAndVariantsId(PurchaseOrders purchaseOrdersId, ProductVariants variantsId);

    List<PurchaseOrderItems> findByPurchaseOrdersId_PurchaseOrdersId(UUID purchaseOrdersId);

    @Query("SELECT SUM(poi.recivedQuantity) " +
            "FROM PurchaseOrderItems poi " +
            "WHERE poi.purchaseOrdersId.status = 'received' " +
            "AND poi.purchaseOrdersId.orderDate BETWEEN :startDate AND :endDate")
    Integer getTotalReceivedQuantity(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);


    // تغییر متد به صورت زیر با استفاده از مسیر درون موجودیت‌ها
    @Query("SELECT poi FROM PurchaseOrderItems poi " +
            "JOIN poi.purchaseOrdersId po " +
            "WHERE poi.variantsId = :variant " +
            "AND po.status = :status " +
            "ORDER BY po.orderDate ASC")
    List<PurchaseOrderItems> findReceivedItemsByVariant(
            @Param("variant") ProductVariants variant,
            @Param("status") Statuses status);


}
