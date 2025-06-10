package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ProductImage {
    @Id
    @GeneratedValue
    private UUID imageId;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonIgnoreProperties({"productName", "productDescription","categories","brands", "sku", "price", "costPrice", "weight", "createdAt", "updatedAt", "active","length","width","height", "createdBy", "updatedBy"})
    private Products productId;

    private String imageUrl;

    private String altText;

    private Integer sortOrder;

    private boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    private LocalDateTime createdAt;

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserAccounts getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccounts createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
