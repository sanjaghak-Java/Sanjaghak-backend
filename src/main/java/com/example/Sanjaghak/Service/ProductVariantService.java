package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.ProductVariantsRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public ProductVariants createProductVariants(ProductVariants productVariants, UUID productId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if(!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("کالا مورد نظر یافت نشد");
        }

        boolean skuExists = productVariantsRepository.existsBySkuIgnoreCase(productVariants.getSku());
        if (skuExists) {
            throw new IllegalArgumentException("این SKU قبلاً ثبت شده است.");
        }

        Products existing = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        boolean colorExists = productVariantsRepository.existsByProductIdAndColorIgnoreCase(existing, productVariants.getColor());
        if (colorExists) {
            throw new IllegalArgumentException("این رنگ قبلاً برای این محصول ثبت شده است.");
        }

        boolean hexExists = productVariantsRepository.existsByProductIdAndHexadecimalIgnoreCase(existing, productVariants.getHexadecimal());
        if (hexExists) {
            throw new IllegalArgumentException("کد رنگ هگزادسیمال وارد شده قبلاً برای این محصول ثبت شده است.");
        }

        productVariants.setProductId(existing);
        productVariants.setCreatedAt(LocalDateTime.now());
        productVariants.setUpdatedAt(LocalDateTime.now());

        return productVariantsRepository.save(productVariants);
    }

public ProductVariants updateProductVariant(UUID productVariantId,ProductVariants productVariants,UUID productId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        if (!productVariantsRepository.existsById(productVariantId)) {
            throw new IllegalArgumentException("کالای مورد نظر یافت نشد.");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        ProductVariants existing = productVariantsRepository.findById(productVariantId)
                .orElseThrow(() -> new EntityNotFoundException("کالا مورد نظر پیدا نشد !"));

        if(!productVariants.getSku().equalsIgnoreCase(existing.getSku())) {
            boolean skuExists = productVariantsRepository.existsBySkuIgnoreCase(productVariants.getSku());
            if (skuExists) {
                throw new IllegalArgumentException("این SKU قبلاً ثبت شده است.");
            }
        }

        if(!productVariants.getColor().equalsIgnoreCase(existing.getColor())) {
            boolean colorExists = productVariantsRepository.existsByProductIdAndColorIgnoreCase(products, productVariants.getColor());
            if (colorExists) {
                throw new IllegalArgumentException("این رنگ قبلاً برای این محصول ثبت شده است.");
            }
        }

        if(!productVariants.getHexadecimal().equalsIgnoreCase(existing.getHexadecimal())) {
            boolean hexExists = productVariantsRepository.existsByProductIdAndHexadecimalIgnoreCase(products, productVariants.getHexadecimal());
            if (hexExists) {
                throw new IllegalArgumentException("کد رنگ هگزادسیمال وارد شده قبلاً برای این محصول ثبت شده است.");
            }
        }

        existing.setProductId(products);
        existing.setSku(productVariants.getSku());
        existing.setPrice(productVariants.getPrice());
        existing.setCostPrice(productVariants.getCostPrice());
        existing.setColor(productVariants.getColor());
        existing.setHexadecimal(productVariants.getHexadecimal());
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        return productVariantsRepository.save(existing);
    }

    public ProductVariants getProductVariantById(UUID productVariantId) {
        return productVariantsRepository.findById(productVariantId).orElseThrow(()-> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));
    }

    public List<ProductVariants> getAllProductVariants() {
        return productVariantsRepository.findAll();
    }

    public List<ProductVariants> getProductVariantsByProductId(UUID productId) {
        return productVariantsRepository.findByProductId_productId(productId);
    }

    public void deleteProductVariant(UUID productVariantId,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if (!productVariantsRepository.existsById(productVariantId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        ProductVariants delete = productVariantsRepository.findById(productVariantId).orElseThrow((() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !")));
        productVariantsRepository.delete(delete);
    }

}
