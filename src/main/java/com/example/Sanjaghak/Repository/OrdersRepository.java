package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.model.Customer;
import com.example.Sanjaghak.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, UUID>, JpaSpecificationExecutor<Orders> {
    boolean existsByOrderNumber(String orderNumber);

    Optional<Orders> findByCustomerIdAndOrderStatus(Customer customerId, OrderStatus orderStatus);

    // پیدا کردن سفارش‌های با وضعیت processing
    List<Orders> findByOrderStatus(OrderStatus orderStatus);

    // پیدا کردن سفارش‌های با وضعیت processing که تمام آیتم‌هایشان در InventoryMovement وجود دارد
    @Query("SELECT o FROM Orders o " +
            "WHERE o.orderStatus = com.example.Sanjaghak.Enum.OrderStatus.processing " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM OrderItem oi " +
            "    WHERE oi.orderId = o " +
            "    AND NOT EXISTS (" +
            "        SELECT 1 FROM InventoryMovement im " +
            "        WHERE im.refrenceId = o.orderId " +
            "        AND im.movementType = com.example.Sanjaghak.Enum.MovementType.ORDER" +
            "        AND im.variantsId = oi.variantId" +
            "    )" +
            ")")
    List<Orders> findProcessingOrdersWithCompleteInventoryMovements();

    List<Orders> findAllByCustomerIdAndOrderStatus(Customer customerId, OrderStatus orderStatus);


    @Query("select coalesce(sum(o.subTotal),0), coalesce(sum(o.shippingCost),0), coalesce(sum(o.taxAmount),0), coalesce(sum(o.discountAmount),0), coalesce(sum(o.totalAmount),0) " +
            "from Orders o where o.orderStatus='delivered' and o.createdAt between :startDate and :endDate")
    Object[] getOrderSums(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
    SELECT o FROM Orders o
    WHERE o.orderStatus = 'delivered'
      AND o.createdAt BETWEEN :startDate AND :endDate
""")

    List<Orders> findByOrderStatusAndCreatedAtBetween(
            OrderStatus status, LocalDateTime start, LocalDateTime end);





}
