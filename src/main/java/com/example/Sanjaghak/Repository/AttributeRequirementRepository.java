package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.AttributeRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttributeRequirementRepository extends JpaRepository<AttributeRequirement, UUID> {
    List<AttributeRequirement> findByCategoryId_CategoryIdAndIsRequiredTrue(UUID categoryId);
    boolean existsByAttributeId_attributeId(UUID attributeId);

}
