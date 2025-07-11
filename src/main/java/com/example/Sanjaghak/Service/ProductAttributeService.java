package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.AttributeRequirementRepository;
import com.example.Sanjaghak.Repository.ProductAttributeRepository;
import com.example.Sanjaghak.Repository.ProductAttributeValueRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Specification.AttributeSpecifications;
import com.example.Sanjaghak.model.ProductAttribute;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProductAttributeService {
    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private ProductAttributeValueRepository productAttributeValueRepository;

    @Autowired
    private AttributeRequirementRepository attributeRequirementRepository;


    @Transactional
    public ProductAttribute createProductAttribute(ProductAttribute productAttribute,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        productAttribute.setCreatedBy(user);
        productAttribute.setCreatedAt(LocalDateTime.now());
        return productAttributeRepository.save(productAttribute);

    }

    public ProductAttribute updateProductAttribute(UUID attributeId,ProductAttribute productAttribute,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productAttributeRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        ProductAttribute existing = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));


        existing.setAttributeName(productAttribute.getAttributeName());
        existing.setAttributeType(productAttribute.getAttributeType());

        return productAttributeRepository.save(existing);
    }

    public ProductAttribute getProductAttributeById(UUID attributeId) {
        return productAttributeRepository.findById(attributeId)
                .orElseThrow(()-> new EntityNotFoundException("ویژگی مورد نظر پیدا نشد !"));
    }

    public Page<ProductAttribute> getAllProductAttributes(String attributeName, Pageable pageable) {
        return productAttributeRepository.findAll(
                AttributeSpecifications.filterAttribute(attributeName), pageable);
    }

    public void deleteProductAttribute(UUID attributeId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!productAttributeRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        boolean isUsed = productAttributeValueRepository.existsByAttributeId_attributeId(attributeId) || attributeRequirementRepository.existsByAttributeId_attributeId(attributeId);


        if (isUsed) {
            throw new IllegalStateException("این ویژگی در بخش‌های دیگر استفاده شده و قابل حذف نیست");
        }

        ProductAttribute delete = productAttributeRepository.findById(attributeId).orElseThrow((() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !")));
        productAttributeRepository.delete(delete);

    }

}
