package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CategoryImageRepository;
import com.example.Sanjaghak.Repository.CategoryRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryImageService {
    @Autowired
    private CategoryImageRepository categoryImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public CategoryImage handleImageUpload(MultipartFile file, UUID CategoryId, String altText, String token) throws IOException {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);


        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }


        Categories category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new RuntimeException("دسته بندی مورد نظر یافت نشد"));

        String originalFilename = file.getOriginalFilename();
        String imageUrl = "/uploads/" + originalFilename;

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        if (imageExists(imageUrl, category)) {
            throw new RuntimeException("این تصویر قبلاً آپلود شده است.");
        }

        Path uploadPath = Paths.get("D:/springbootproject/Sanjaghak/Sanjaghak-backend/media");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destination = uploadPath.resolve(originalFilename);
        file.transferTo(destination.toFile());

        CategoryImage image = new CategoryImage();
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        image.setCategories(category);
        image.setCreatedBy(user);
        image.setCreatedAt(LocalDateTime.now());

        return  categoryImageRepository.save(image);
    }

    public boolean imageExists(String imageUrl, Categories category) {
        return categoryImageRepository.existsByImageUrlAndCategories(imageUrl, category);
    }

    public CategoryImage update(UUID imageId,UUID categoryId, ProductImage updatedImage, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("دسته بندی  مورد نظر یافت نشد.");
        }

        CategoryImage existing = categoryImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("تصویر مورئ نظر یافت نشد!"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Categories categories = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));


        existing.setAltText(updatedImage.getAltText());
        existing.setImageUrl(updatedImage.getImageUrl());
        existing.setCategories(categories);
        return categoryImageRepository.save(existing);
    }

    public List<CategoryImage> findAll() {
        return categoryImageRepository.findAll();
    }

    public List<CategoryImage> findByCategoryId(UUID categoryId) {
        Categories category = categoryRepository.findById(categoryId) .orElseThrow(() -> new RuntimeException("تصویر مورئ نظر یافت نشد!"));
        List<CategoryImage> images = categoryImageRepository.findByCategories(category);
        return images;
    }

    public void delete(UUID imageId,String token) {
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!categoryImageRepository.existsById(imageId)) {
            throw new RuntimeException("تصویر مورئ نظر یافت نشد!");
        }
        categoryImageRepository.deleteById(imageId);
    }





}
