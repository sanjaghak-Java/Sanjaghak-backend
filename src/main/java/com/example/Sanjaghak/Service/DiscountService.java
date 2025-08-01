package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.DiscountRepository;
import com.example.Sanjaghak.Repository.ProductVariantsRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    public Discount createDiscount(Discount discount, UUID variantId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ProductVariants productVariants = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر یافت نشد!"));

        // تنظیم اجباری برخی مقادیر اولیه (اگر لازم بود)
        if (discount.getStartFrom() == null || discount.getEndFrom() == null) {
            throw new RuntimeException("زمان شروع و پایان تخفیف باید مشخص باشد.");
        }

        // بررسی وجود تخفیف فعال قبلی برای این محصول
        List<Discount> existingDiscounts = discountRepository.findByVariantsIdAndIsActiveTrue(productVariants);

        for (Discount existing : existingDiscounts) {
            // اگر endFrom تخفیف قبلی بعد یا برابر با startFrom تخفیف جدید باشد، اجازه ثبت ندیم
            if (!existing.getEndFrom().isBefore(discount.getStartFrom())) {
                throw new RuntimeException("برای این محصول، تخفیف فعالی در بازه زمانی تعیین‌شده وجود دارد.");
            }
        }

        discount.setVariantsId(productVariants);
        return discountRepository.save(discount);
    }

    public Discount updateDiscount(UUID discountId, Discount updatedDiscount, UUID variantId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("تخفیف مورد نظر پیدا نشد."));

        ProductVariants newVariant = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر یافت نشد."));

        LocalDateTime newStartFrom = updatedDiscount.getStartFrom();
        LocalDateTime newEndFrom = updatedDiscount.getEndFrom();

        if (newStartFrom == null || newEndFrom == null) {
            throw new RuntimeException("تاریخ شروع و پایان نمی‌توانند خالی باشند.");
        }

        // بررسی تغییر واقعی
        boolean variantChanged = !existingDiscount.getVariantsId().getVariantId().equals(newVariant.getVariantId());
        boolean startChanged = !existingDiscount.getStartFrom().isEqual(newStartFrom);
        boolean endChanged = !existingDiscount.getEndFrom().isEqual(newEndFrom);

        if (variantChanged || startChanged || endChanged) {
            List<Discount> activeDiscounts = discountRepository.findByVariantsIdAndIsActiveTrue(newVariant);

            for (Discount d : activeDiscounts) {
                if (!d.getDiscountId().equals(discountId)) {
                    // بررسی دقیق تداخل (حتی با برابری دقیق)
                    boolean overlap = !(newEndFrom.isBefore(d.getStartFrom()) || newStartFrom.isAfter(d.getEndFrom()));
                    if (overlap) {
                        throw new RuntimeException("برای این محصول تخفیف فعالی وجود دارد که با بازه زمانی جدید تداخل دارد.");
                    }
                }
            }
        }

        if(updatedDiscount.getActive() == null){
            updatedDiscount.setActive(true);
        }

        // فقط حالا برو سراغ اعمال تغییرات
        existingDiscount.setVariantsId(newVariant);
        existingDiscount.setStartFrom(newStartFrom);
        existingDiscount.setEndFrom(newEndFrom);
        existingDiscount.setDiscountPercentage(updatedDiscount.getDiscountPercentage());
        existingDiscount.setDiscountDescription(updatedDiscount.getDiscountDescription());
        existingDiscount.setActive(updatedDiscount.getActive());

        return discountRepository.save(existingDiscount);
    }

    public Discount getDiscountById(UUID discountId ) {
        return discountRepository.findById(discountId).orElseThrow(()-> new EntityNotFoundException("قفسه مورد نظر پیدا نشد !"));
    }

    public List<Discount> getAllDiscount( ) {
        return discountRepository.findAll();
    }

    public Discount getCurrentActiveDiscount(UUID variantId) {
        LocalDateTime now = LocalDateTime.now();

        return discountRepository.findActiveDiscountByVariantAndNow(variantId, now)
                .orElse(null);
    }


    public Discount getMaxActiveDiscountByProduct(UUID productId) {
        List<Discount> discounts = discountRepository.findTopDiscountByProductOrderByPercentageDesc(productId);
        if (discounts.isEmpty()) {
            return null;
        }
        return discounts.get(0); // بیشترین تخفیف
    }







}
