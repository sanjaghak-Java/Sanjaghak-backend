package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.OrderItem;
import com.example.Sanjaghak.model.Orders;
import com.example.Sanjaghak.model.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>, JpaSpecificationExecutor<OrderItem> {

    boolean existsByOrderIdAndVariantId (Orders orderId, ProductVariants vendorId);

    List<OrderItem> findByOrderId(Orders orderId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) " +
            "FROM OrderItem oi " +
            "JOIN oi.orderId o " +
            "WHERE o.orderStatus = 'delivered' AND oi.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalQuantity(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
    SELECT oi FROM OrderItem oi
    WHERE oi.variantId = :variantId
      AND oi.createdAt < :startDate
""")

    List<OrderItem> findByVariantIdAndOrderIdCreatedAtBefore(
            ProductVariants variant, LocalDateTime beforeDate);



}
