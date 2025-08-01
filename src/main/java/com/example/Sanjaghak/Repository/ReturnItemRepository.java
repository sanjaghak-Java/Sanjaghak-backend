package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.OrderItem;
import com.example.Sanjaghak.model.Return;
import com.example.Sanjaghak.model.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, UUID>, JpaSpecificationExecutor<ReturnItem> {
    List<ReturnItem> findByReturnId(Return returnId);
    List<ReturnItem> findByReturnIdAndRestockTrue(Return returnId);
    boolean existsByReturnIdAndRestockTrue(Return returnObj);

    @Query("""
    SELECT ri.orderItemId.orderItemId, SUM(ri.quantity) as totalQuantity,
           SUM(ri.quantity * ri.orderItemId.unitPrice) as totalPrice,
           SUM((ri.orderItemId.discountAmount / ri.orderItemId.quantity) * ri.quantity) as totalDiscount
    FROM ReturnItem ri
    JOIN ri.returnId r
    WHERE r.returnStatus = 'CHECKED'
      AND r.createdAt BETWEEN :startDate AND :endDate
      AND ri.restock = true
    GROUP BY ri.orderItemId.orderItemId
""")
    List<Object[]> findReturnSummaryBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query("""
    SELECT ri FROM ReturnItem ri
    JOIN ri.returnId r
    WHERE ri.orderItemId = :orderItemId
      AND ri.restock = true
      AND r.returnStatus = 'CHECKED'
""")
    List<ReturnItem> findReturnsByOrderItem(@Param("orderItemId") OrderItem orderItemId);

}
