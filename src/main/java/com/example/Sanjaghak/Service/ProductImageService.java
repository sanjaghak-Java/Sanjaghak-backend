package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.ProductImageRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.ProductImage;
import com.example.Sanjaghak.model.Products;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public void save(ProductImage image) {
        productImageRepository.save(image);
    }

    public ProductImage update(UUID imageId,UUID productId, ProductImage updatedImage, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        ProductImage existing = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("تصویر مورئ نظر یافت نشد!"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));


        existing.setAltText(updatedImage.getAltText());
        existing.setImageUrl(updatedImage.getImageUrl());
        existing.setPrimary(updatedImage.isPrimary());
        existing.setSortOrder(updatedImage.getSortOrder());
        existing.setProductId(products);
        return productImageRepository.save(existing);
    }

    public ProductImage handleImageUpload(MultipartFile file, UUID productId, String altText,String token) throws IOException {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);


        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }


        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("محصول یافت نشد"));

        String originalFilename = file.getOriginalFilename();
        String imageUrl = "/uploads/" + originalFilename;

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        if (imageExists(imageUrl, product)) {
            throw new RuntimeException("این تصویر قبلاً آپلود شده است.");
        }

        Path uploadPath = Paths.get("D:/springbootproject/Sanjaghak/Sanjaghak-backend/media");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destination = uploadPath.resolve(originalFilename);
        file.transferTo(destination.toFile());

        ProductImage image = new ProductImage();
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        image.setProductId(product);
        image.setCreatedBy(user);
        image.setCreatedAt(LocalDateTime.now());
        image.setSortOrder(getNextSortOrderForProduct(productId));
        image.setPrimary(false);

        return  productImageRepository.save(image);
    }


    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    public List<ProductImage> findByProductId(UUID productId) {
        Products product = productRepository.findById(productId) .orElseThrow(() -> new RuntimeException("تصویر مورئ نظر یافت نشد!"));
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product);
        return images;
    }

    public void delete(UUID imageId,String token) {
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productImageRepository.existsById(imageId)) {
            throw new RuntimeException("تصویر مورئ نظر یافت نشد!");
        }
        productImageRepository.deleteById(imageId);
    }

    public int getNextSortOrderForProduct(UUID productId) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("محصول یافت نشد!"));
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderDesc(product);
        if (images.isEmpty()) {
            return 0;
        } else {
            return images.get(0).getSortOrder() + 1;
        }
    }
    public boolean imageExists(String imageUrl, Products product) {
        return productImageRepository.existsByImageUrlAndProductId(imageUrl, product);
    }



}
