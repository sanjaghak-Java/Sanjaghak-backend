package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.OrderItem;
import com.example.Sanjaghak.model.Orders;
import com.example.Sanjaghak.model.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>, JpaSpecificationExecutor<OrderItem> {

    boolean existsByOrderIdAndVariantId (Orders orderId, ProductVariants vendorId);

    List<OrderItem> findByOrderId(Orders orderId);
}
