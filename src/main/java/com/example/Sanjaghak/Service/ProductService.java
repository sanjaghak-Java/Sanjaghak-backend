package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.BrandsRepository;
import com.example.Sanjaghak.Repository.CategoryRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.model.Brands;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.Products;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandsRepository brandRepository;

    public Products createProduct(Products product, UUID categoryId, UUID brandId) {


        if(!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته بندی مورد نظر یافت نشد");
        }

        if(!brandRepository.existsByBrandId(brandId)) {
            throw new IllegalArgumentException("برند مورد نظر یافت نشد");
        }

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new EntityNotFoundException("دسته بندی مورد نظر پیدا نشد !"));

        Brands brands = brandRepository.findById(brandId)
                .orElseThrow(()-> new EntityNotFoundException("برند مورد نظر یافت نشد !"));



        product.setCategories(category);
        product.setBrands(brands);
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }


    public Products updateProduct(UUID productId, Products updatedProduct, UUID categoryId, UUID brandId) {

        // بررسی وجود محصول
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }

        // بررسی وجود دسته‌بندی
        if (!categoryRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException("دسته‌بندی مورد نظر یافت نشد.");
        }

        // بررسی وجود برند
        if (!brandRepository.existsByBrandId(brandId)) {
            throw new IllegalArgumentException("برند مورد نظر یافت نشد.");
        }
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
        existing.setUpdatedBy(updatedProduct.getUpdatedBy() );
        existing.setActive(updatedProduct.isActive());
        existing.setCreatedBy(updatedProduct.getCreatedBy());


        existing.setCategories(category);
        existing.setBrands(brand);
        existing.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existing);
    }

    public Products getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(()-> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }


}
