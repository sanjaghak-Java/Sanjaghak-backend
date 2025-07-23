package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.ProductAttributeRepository;
import com.example.Sanjaghak.Repository.ProductAttributeValueRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductAttributeValueService {

    @Autowired
    private ProductAttributeValueRepository productAttributeValueRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;


    public ProductAttributeValue createProductAttributeValue(UUID productId, UUID attributeId, ProductAttributeValue productAttributeValue, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        if (!productAttributeRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("ویژگی مورد نظر یافت نشد.");
        }

        if (productAttributeValueRepository.existsByProductId_productIdAndAttributeId_attributeId(productId, attributeId)) {
            throw new IllegalArgumentException("تکراری");
        }


        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("ویژگی مورد نظر یافت نشد!"));

        Products products = productRepository.findById(productId).
        orElseThrow(() -> new RuntimeException("محصول مورد نظر یافت نشد!"));

        Categories categories = products.getCategories();



        productAttributeValue.setCreatedBy(user);
        productAttributeValue.setCreatedAt(LocalDateTime.now());
        productAttributeValue.setProductId(products);
        productAttributeValue.setAttributeId(attribute);
        return productAttributeValueRepository.save(productAttributeValue);
    }

    public ProductAttributeValue updateProductAttributeValue(UUID attributeValueId,UUID productId,UUID attributeId,ProductAttributeValue productAttributeValue,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productAttributeValueRepository.existsById(attributeValueId)) {
            throw new IllegalArgumentException("ارزش ویژگی مورد نظر یافت نشد.");
        }

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        if (!productAttributeRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("ویژگی مورد نظر یافت نشد.");
        }

        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new EntityNotFoundException("ویژگی مورد نظر پیدا نشد !"));

        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        ProductAttributeValue existing = productAttributeValueRepository.findById(attributeValueId)
                .orElseThrow(() -> new EntityNotFoundException("  ارزش ویژگی مورد نظر پیدا نشد !"));

        existing.setProductId(products);
        existing.setAttributeId(attribute);
        existing.setValue(productAttributeValue.getValue());

        return productAttributeValueRepository.save(existing);
    }

    public ProductAttributeValue getProductAttributeValueById(UUID attributeValueId) {
        return productAttributeValueRepository.findById(attributeValueId)
                .orElseThrow(()-> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));
    }


    public List<ProductAttributeValue> getProductAttributeValueByProductId(UUID productId) {
        return productAttributeValueRepository.findByProductId_productId(productId);
    }

    public List<ProductAttributeValue> getAllProductAttributeValues() {
        return productAttributeValueRepository.findAll();
    }

    public void deleteProductAttributeValue(UUID attributeValueId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if (!productAttributeValueRepository.existsById(attributeValueId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        ProductAttributeValue delete = productAttributeValueRepository.findById(attributeValueId).orElseThrow((() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !")));
        productAttributeValueRepository.delete(delete);

    }


}
