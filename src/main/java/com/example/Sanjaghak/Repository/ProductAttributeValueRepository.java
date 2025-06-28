package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, UUID> {
    List<ProductAttributeValue> findByProductId_productId(UUID productId);

    boolean existsByProductId_productId(UUID productId);
    boolean existsByAttributeId_attributeId(UUID attributeId);

}
