package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, UUID> {
    boolean existsByCategoryName(String categoryName);
    boolean existsByCategoryId(UUID categoryId);
    List<Categories> findByActiveTrue();
}
