package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Brands;
import com.example.Sanjaghak.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Products, UUID>, JpaSpecificationExecutor<Products> {
    List<Products> findByActiveTrue();
    boolean existsByCategories_CategoryId(UUID categoryId);
    boolean existsByBrands_BrandId(UUID brandId);

    @Query("SELECT DISTINCT p.brands FROM Products p WHERE p.categories.categoryId = :categoryId")
    List<Brands> findDistinctBrandsByCategoryId(@Param("categoryId") UUID categoryId);
}

