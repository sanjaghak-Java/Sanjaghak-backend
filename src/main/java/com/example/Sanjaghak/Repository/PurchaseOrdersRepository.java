package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, UUID>, JpaSpecificationExecutor<PurchaseOrders> {
    boolean existsByOrderNumber(String orderNumber);
}
