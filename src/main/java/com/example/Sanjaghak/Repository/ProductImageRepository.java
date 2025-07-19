package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.ProductImage;
import com.example.Sanjaghak.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Products product);
    List<ProductImage> findByProductIdOrderBySortOrderDesc(Products product);
    boolean existsByImageUrlAndProductId(String imageUrl, Products product);
    boolean existsByProductIdAndPrimaryTrue(Products product);
    boolean existsByProductIdAndSortOrderAndImageIdNot(Products product, Integer sortOrder, UUID imageId);



}
