package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, UUID>, JpaSpecificationExecutor<Orders> {
    boolean existsByOrderNumber(String orderNumber);
}
