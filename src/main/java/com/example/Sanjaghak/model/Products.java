package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.UUID;


@Entity
public class Products {
    @Id
    @GeneratedValue
    private UUID productId;

    private String productName;

    private String productDescription;

//    @Column(unique = true)
//    private String sku;

    private String model;

//    private BigDecimal price;
//
//    private BigDecimal costPrice;

    private boolean active;

    private BigDecimal weight;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"categoryName", "categoryDescription", "createdBy", "updatedBy", "createdAt", "updatedAt", "active"})
    private Categories categories;

    @ManyToOne
    @JoinColumn(name ="brands_id")
    @JsonIgnoreProperties({"brandName", "brandDescription", "logoUrl", "websiteUrl", "createdAt", "updatedAt", "active"})
    private Brands brands;

    public Brands getBrands() {
        return brands;
    }

    public void setBrands(Brands brands) {
        this.brands = brands;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

//    public BigDecimal getCostPrice() {
//        return costPrice;
//    }
//
//    public void setCostPrice(BigDecimal costPrice) {
//        this.costPrice = costPrice;
//    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

//    public String getSku() {
//        return sku;
//    }
//
//    public void setSku(String sku) {
//        this.sku = sku;
//    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public UserAccounts getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccounts createdBy) {
        this.createdBy = createdBy;
    }

    public UserAccounts getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserAccounts updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
