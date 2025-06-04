package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CategoryRepository;
import com.example.Sanjaghak.model.Categories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public String createCategory(Categories category) {

        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            return "دسته‌بندی از قبل وجود دارد !";
        }

        category.setCreatedAt(LocalDateTime.now());
        category.setActive(true);
        categoryRepository.save(category);
        return "دسته‌بندی جدید ایجاد شد !";
    }


    public Categories updateCategory(UUID id, Categories category) {
        Categories oldCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        oldCategory.setCategoryName(category.getCategoryName());
        oldCategory.setCategoryDescription(category.getCategoryDescription());
        oldCategory.setActive(category.isActive());
        oldCategory.setCreatedBy(category.getCreatedBy());
        oldCategory.setUpdatedBy(category.getUpdatedBy());
        oldCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(oldCategory);
    }

    public Categories getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));
    }

    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }
}
