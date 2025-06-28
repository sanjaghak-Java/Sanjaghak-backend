package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.BrandsRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Specification.BrandSpecification;
import com.example.Sanjaghak.model.Brands;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BrandService {

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private ProductRepository productRepository;

    public Brands createBrand(Brands brand,String token) {
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (brandsRepository.existsByBrandName(brand.getBrandName())) {
            throw new IllegalArgumentException("برند مورد نظر از قبل وجود دارد !");
        }

        brand.setCreatedAt(LocalDateTime.now());
        brand.setActive(true);
        return brandsRepository.save(brand);
    }


    public Brands updateBrand(UUID id, Brands brand, String token) {
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if(!brandsRepository.existsById(id)) {
            throw new IllegalArgumentException("برند مورد نظر پیدا نشد !");
        }
        Brands brands= brandsRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("برند مورد نظر یافت نشد !"));
        brands.setBrandName(brand.getBrandName());
        brands.setBrandDescription(brand.getBrandDescription());
        brands.setActive(brand.isActive());
        brands.setUpdatedAt(LocalDateTime.now());
        brands.setLogoUrl(brand.getLogoUrl());
        brands.setWebsiteUrl(brand.getWebsiteUrl());
        return brandsRepository.save(brands);
    }

    public List<Brands> getAllBrands() {
        return brandsRepository.findAll();
    }

    public Brands getBrandById(UUID id) {
        return brandsRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("برند مورد نظر یافت نشد !"));
    }

    public List<Brands> getActiveBrands() {
        return brandsRepository.findByActiveTrue();
    }

    public Page<Brands> getPaginationBrands(String brandName, Pageable pageable) {
        return brandsRepository.findAll(BrandSpecification.filterBrand(brandName), pageable);
    }

    public void deleteBrand(UUID brandId, String token) {
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Brands brands = brandsRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("برند مورد نظر پیدا نشد !"));

        boolean isUsed = productRepository.existsByBrands_BrandId(brandId);

        if (isUsed) {
            throw new IllegalStateException("این برند در بخش‌های دیگر استفاده شده و قابل حذف نیست");
        }

        brandsRepository.delete(brands);
    }

}
