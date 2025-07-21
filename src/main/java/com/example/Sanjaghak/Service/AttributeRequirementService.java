package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.AttributeRequirementRepository;
import com.example.Sanjaghak.Repository.CategoryRepository;
import com.example.Sanjaghak.Repository.ProductAttributeRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributeRequirementService {
    @Autowired
    private AttributeRequirementRepository attributeRequirementRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public AttributeRequirement createAttributeRequirement(AttributeRequirement attributeRequirement,
                                                           UUID categoryId,
                                                           UUID productAttributeId,
                                                           String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if(!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته بندی مورد نظر یافت نشد");
        }

        if(!productAttributeRepository.existsById(productAttributeId)) {
            throw new IllegalArgumentException("ویژگی مورد نظر یافت نشد");
        }

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(()-> new EntityNotFoundException("ویژگی  مورد نظر پیدا نشد !"));

        attributeRequirement.setAttributeId(productAttribute);
        attributeRequirement.setCategoryId(category);

        return attributeRequirementRepository.save(attributeRequirement);
    }

    public AttributeRequirement updateAttributeRequirement(UUID AttRequirementId, AttributeRequirement attributeRequirement, UUID categoryId, UUID productAttributeId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!attributeRequirementRepository.existsById(AttRequirementId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        if (!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته‌بندی مورد نظر یافت نشد.");
        }

        if (!productAttributeRepository.existsById(productAttributeId)) {
            throw new IllegalArgumentException("ویژگی مورد نظر یافت نشد.");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        AttributeRequirement existing = attributeRequirementRepository.findById(AttRequirementId)
                .orElseThrow(() -> new EntityNotFoundException("واجب بودن ارزش ویژگی مورد نظر پیدا نشد !"));

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(()-> new EntityNotFoundException("ویژگی  مورد نظر پیدا نشد !"));

        existing.setAttributeId(productAttribute);
        existing.setCategoryId(category);
        existing.setRequired(attributeRequirement.getRequired());

        return attributeRequirementRepository.save(existing);
    }

    public AttributeRequirement getAttributeRequirementById(UUID AttRequirementId) {
        return attributeRequirementRepository.findById(AttRequirementId).
                orElseThrow(()-> new EntityNotFoundException("واجب بودن ارزش ویژگی مورد نظر پیدا نشد !"));
    }

    public List<AttributeRequirement> getAllAttributeRequirements() {
        return attributeRequirementRepository.findAll();
    }

    public void deleteProductAttributeRequirement(UUID attributeId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if (!attributeRequirementRepository.existsById(attributeId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        AttributeRequirement delete = attributeRequirementRepository.findById(attributeId).orElseThrow((() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !")));
        attributeRequirementRepository.delete(delete);
    }

    public List<Map<String, Object>> getRequiredAttributeRequirementByCategory(UUID categoryId) {
        if (!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته‌بندی مورد نظر یافت نشد");
        }

        List<AttributeRequirement> requiredAttributes = attributeRequirementRepository
                .findByCategoryId_CategoryIdAndIsRequiredTrue(categoryId);

        if (requiredAttributes.isEmpty()) {
            throw new IllegalArgumentException("هیچ ویژگی ضروری‌ای برای این دسته‌بندی تعریف نشده است.");
        }

        List<Map<String, Object>> result = requiredAttributes.stream().map(req -> {
            Map<String, Object> map = new HashMap<>();
            map.put("requirementId", req.getId());
            map.put("attribute", req.getAttributeId());
            return map;
        }).collect(Collectors.toList());

        Collections.reverse(result);

        return result;
    }




}
