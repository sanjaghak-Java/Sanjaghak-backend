package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Discount;
import com.example.Sanjaghak.model.ProductVariants;
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
public interface DiscountRepository extends JpaRepository<Discount, UUID>, JpaSpecificationExecutor<Discount> {
    List<Discount> findByVariantsIdAndIsActiveTrue(ProductVariants variantsId);
    @Query("SELECT d FROM Discount d WHERE d.variantsId.variantId = :variantId AND d.isActive = true AND :now BETWEEN d.startFrom AND d.endFrom")
    Optional<Discount> findActiveDiscountByVariantAndNow(@Param("variantId") UUID variantId, @Param("now") LocalDateTime now);

    @Query("""
    SELECT d FROM Discount d
    WHERE d.isActive = true
      AND d.startFrom <= CURRENT_TIMESTAMP
      AND d.endFrom >= CURRENT_TIMESTAMP
      AND d.variantsId.isActive = true
      AND d.variantsId.productId.productId = :productId
    ORDER BY d.discountPercentage DESC
""")
    List<Discount> findTopDiscountByProductOrderByPercentageDesc(@Param("productId") UUID productId);






}
