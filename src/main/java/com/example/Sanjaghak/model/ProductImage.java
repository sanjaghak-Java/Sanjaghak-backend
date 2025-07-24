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
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"productName","productDescription","model","active","weight","length","width","height","createdBy","updatedBy","categories","brands","createdAt", "updatedAt"})
    private Products productId;

    private String imageUrl;

    private String altText;

    private Integer sortOrder;

    @Column(name = "is_primary")
    private boolean primary;

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

    public boolean getPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
