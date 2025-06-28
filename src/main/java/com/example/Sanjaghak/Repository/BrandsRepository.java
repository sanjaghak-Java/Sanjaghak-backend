package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Brands;
import com.example.Sanjaghak.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface BrandsRepository extends JpaRepository<Brands, UUID>, JpaSpecificationExecutor<Brands> {
    boolean existsByBrandName(String brandName);
    boolean existsByBrandId(UUID id);
    List<Brands> findByActiveTrue();

}
