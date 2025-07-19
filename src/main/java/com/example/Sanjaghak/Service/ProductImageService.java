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
                .orElseThrow(() -> new RuntimeException("تصویر مورد نظر یافت نشد!"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        if (updatedImage.getPrimary() == true) {
            boolean alreadyRequiredExists = productImageRepository
                    .existsByProductIdAndPrimaryTrue(products);
            if (alreadyRequiredExists) {
                throw new RuntimeException("برای این محصول قبلاً یک تصویر اصلی ثبت شده است.");
            }
        }

        if (updatedImage.getSortOrder() != null) {
            boolean sortOrderExists = productImageRepository
                    .existsByProductIdAndSortOrderAndImageIdNot(products, updatedImage.getSortOrder(), imageId);
            if (sortOrderExists) {
                throw new IllegalArgumentException("برای این محصول، شماره ترتیب  وارد شده تکراری است.");
            }
        }

        existing.setAltText(updatedImage.getAltText());
        existing.setPrimary(updatedImage.getPrimary());
        existing.setSortOrder(updatedImage.getSortOrder());
        existing.setProductId(products);
        return productImageRepository.save(existing);
    }

    public ProductImage handleImageUpload(MultipartFile file, UUID productId, String altText, boolean required, String token) throws IOException {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }


        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("محصول یافت نشد"));

        if (required) {
            boolean alreadyRequiredExists = productImageRepository
                    .existsByProductIdAndPrimaryTrue(product);
            if (alreadyRequiredExists) {
                throw new RuntimeException("برای این محصول قبلاً یک تصویر اصلی ثبت شده است.");
            }
        }

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
        image.setPrimary(required);
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

    public void delete(UUID imageId, String token) {
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        // پیدا کردن تصویر مورد نظر
        ProductImage imageToDelete = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("تصویر مورد نظر یافت نشد!"));

        Products product = imageToDelete.getProductId();
        int deletedSortOrder = imageToDelete.getSortOrder();

        // حذف فایل فیزیکی از مسیر
        try {
            String fileName = Paths.get(imageToDelete.getImageUrl()).getFileName().toString();
            Path filePath = Paths.get("D:/springbootproject/Sanjaghak/Sanjaghak-backend/media").resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("خطا در حذف فایل فیزیکی: " + e.getMessage());
        }

        // حذف رکورد از دیتابیس
        productImageRepository.deleteById(imageId);

        // آپدیت sortOrder برای تصاویر با sortOrder بیشتر
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product);
        for (ProductImage img : images) {
            if (img.getSortOrder() > deletedSortOrder) {
                img.setSortOrder(img.getSortOrder() - 1);
                productImageRepository.save(img);
            }
        }
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
