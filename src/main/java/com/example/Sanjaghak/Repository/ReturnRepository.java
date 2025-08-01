package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.ReturnStatus;
import com.example.Sanjaghak.model.Orders;
import com.example.Sanjaghak.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ReturnRepository extends JpaRepository<Return, UUID>, JpaSpecificationExecutor<Return> {
    List<Return> findByOrderIdIn(List<Orders> orders);
    List<Return> findByReturnStatus(ReturnStatus returnStatus);
}
