package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.BrandsRepository;
import com.example.Sanjaghak.model.Brands;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BrandService {

    @Autowired
    private BrandsRepository brandsRepository;

    public Brands createBrand(Brands brand) {
        if (brandsRepository.existsByBrandName(brand.getBrandName())) {
            throw new IllegalArgumentException("برند مورد نظر از قبل وجود دارد !");
        }

        brand.setCreatedAt(LocalDateTime.now());
        brand.setActive(true);
        return brandsRepository.save(brand);
    }


    public Brands updateBrand(UUID id, Brands brand) {
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

}
