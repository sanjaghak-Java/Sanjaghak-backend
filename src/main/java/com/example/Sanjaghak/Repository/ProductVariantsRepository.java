package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.ProductVariants;

import com.example.Sanjaghak.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantsRepository extends JpaRepository<ProductVariants, UUID>, JpaSpecificationExecutor<ProductVariants> {
    boolean existsByProductIdAndColorIgnoreCase(Products productId, String color);
    boolean existsByProductIdAndHexadecimalIgnoreCase(Products productId, String hexadecimal);
    boolean existsBySkuIgnoreCase(String sku);
    List<ProductVariants> findByProductId_productId(UUID productId);
}
