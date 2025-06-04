package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface BrandsRepository extends JpaRepository<Brands, UUID> {
    boolean existsByBrandName(String brandName);
    boolean existsByBrandId(UUID id);

}
