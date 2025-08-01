package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Return;
import com.example.Sanjaghak.model.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, UUID>, JpaSpecificationExecutor<ReturnItem> {
    List<ReturnItem> findByReturnId(Return returnId);
    List<ReturnItem> findByReturnIdAndRestockTrue(Return returnId);
    boolean existsByReturnIdAndRestockTrue(Return returnObj);
}
