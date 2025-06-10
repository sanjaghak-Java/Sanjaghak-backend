package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.Specification.ProductSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandsRepository brandRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private AttributeRequirementRepository attributeRequirementRepository;

    @Autowired
    private ProductAttributeValueRepository productAttributeValueRepository;

    public Products createProduct(Products product, UUID categoryId, UUID brandId,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if(!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته بندی مورد نظر یافت نشد");
        }

        if(!brandRepository.existsByBrandId(brandId)) {
            throw new IllegalArgumentException("برند مورد نظر یافت نشد");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        Brands brands = brandRepository.findById(brandId)
                .orElseThrow(()-> new EntityNotFoundException("برند مورد نظر یافت نشد !"));



        product.setCategories(category);
        product.setBrands(brands);
        product.setCreatedAt(LocalDateTime.now());
        product.setCreatedBy(user);
        product.setActive(false);

        return productRepository.save(product);
    }



    public Products updateProduct(UUID productId, Products updatedProduct, UUID categoryId, UUID brandId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        if (!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته‌بندی مورد نظر یافت نشد.");
        }

        if (!brandRepository.existsByBrandId(brandId)) {
            throw new IllegalArgumentException("برند مورد نظر یافت نشد.");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Products existing = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        Brands brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("برند مورد نظر یافت نشد !"));

        existing.setProductName(updatedProduct.getProductName());
        existing.setSku(updatedProduct.getSku());
        existing.setProductDescription(updatedProduct.getProductDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCostPrice(updatedProduct.getCostPrice());
        existing.setWeight(updatedProduct.getWeight());
        existing.setLength(updatedProduct.getLength());
        existing.setWidth(updatedProduct.getWidth());
        existing.setHeight(updatedProduct.getHeight());
        existing.setUpdatedBy(user);
        existing.setActive(updatedProduct.isActive());
        existing.setCategories(category);
        existing.setBrands(brand);
        existing.setModel(updatedProduct.getModel());
        existing.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existing);
    }

    public Products getProductById(UUID productId) {
            return productRepository.findById(productId).orElseThrow(()-> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(UUID attributeId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if (!productRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        Products delete = productRepository.findById(attributeId).orElseThrow((() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !")));
        productRepository.delete(delete);
    }

    @Transactional
    public Object validateAndActivateProduct(UUID productId, UUID categoryId,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد!"));

        List<AttributeRequirement> requiredAttributes = attributeRequirementRepository
                .findByCategoryId_CategoryIdAndIsRequiredTrue(categoryId);

        if (requiredAttributes.isEmpty()) {
            return Map.of("message", "هیچ ویژگی ضروری‌ای برای این دسته‌بندی تعریف نشده است.");
        }

        List<ProductAttributeValue> productAttributes = productAttributeValueRepository
                .findByProductId_productId(productId);

        List<UUID> productAttributeIds = productAttributes.stream()
                .map(pav -> pav.getAttributeId().getAttributeId())
                .toList();

        List<UUID> missingAttributes = requiredAttributes.stream()
                .map(attr -> attr.getAttributeId().getAttributeId())
                .filter(requiredId -> !productAttributeIds.contains(requiredId))
                .toList();

        if (missingAttributes.isEmpty()) {
            product.setActive(true);
            return  productRepository.save(product);
        } else {
            return Map.of("message", "برخی ویژگی‌های ضروری وارد نشده‌اند.",
                    "missingAttributes", missingAttributes);
        }
    }

    public List<Products> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public Page<Products> findProductsByfilter(
            String productName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            UUID categoryId,
            UUID brandId,
            Pageable pageable) {
        return productRepository.findAll(
                ProductSpecifications.filterProducts(
                        productName,
                        minPrice,
                        maxPrice,
                        active,
                        categoryId,
                        brandId
                ),
                pageable
        );
    }
}
