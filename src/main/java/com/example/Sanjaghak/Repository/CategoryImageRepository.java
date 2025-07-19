package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, UUID> {
    boolean existsByImageUrlAndCategories(String imageUrl, Categories categories);
    List<CategoryImage> findByCategories(Categories categories);
    boolean existsByCategories(Categories categories);
}
