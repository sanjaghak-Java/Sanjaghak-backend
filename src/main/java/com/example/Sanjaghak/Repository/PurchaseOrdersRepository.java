package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, UUID>, JpaSpecificationExecutor<PurchaseOrders> {
    boolean existsByOrderNumber(String orderNumber);
    @Query("SELECT " +
            "COALESCE(SUM(po.subTotal), 0), " +
            "COALESCE(SUM(po.shippingCost), 0), " +
            "COALESCE(SUM(po.taxAmount), 0), " +
            "COALESCE(SUM(po.totalAmount), 0) " +
            "FROM PurchaseOrders po " +
            "WHERE po.status = 'received' " +
            "AND po.orderDate BETWEEN :startDate AND :endDate")
    List<Object[]> getOrdersSummary(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
}
