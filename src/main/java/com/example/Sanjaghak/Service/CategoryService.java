package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CategoryRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Specification.CategorySpecifications;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private ProductRepository productRepository;

    public Categories createCategory(Categories category, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("دسته‌بندی از قبل وجود دارد !");
        }

        category.setCreatedBy(user);
        category.setCreatedAt(LocalDateTime.now());
        category.setActive(true);
        return categoryRepository.save(category);
    }

    public Categories updateCategory(UUID id,Categories category, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Categories oldCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        oldCategory.setCategoryName(category.getCategoryName());
        oldCategory.setCategoryDescription(category.getCategoryDescription());
        oldCategory.setActive(category.isActive());
        oldCategory.setUpdatedBy(user);
        oldCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(oldCategory);
    }

    public Categories getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));
    }


    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Categories> getActiveCategories() {
        return  categoryRepository.findByActiveTrue();
    }

    public Page<Categories> getPaginationCategory(String categoryName, Pageable pageable) {
        return categoryRepository.findAll(CategorySpecifications.filterCategory(categoryName),pageable);
    }

    public void deleteCategory(UUID categoryId, String token) {
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        boolean isUsed = productRepository.existsByCategories_CategoryId(categoryId);

        if (isUsed) {
            throw new IllegalStateException("این دسته‌بندی در بخش‌های دیگر استفاده شده و قابل حذف نیست");
        }

        categoryRepository.delete(category);
    }

}
