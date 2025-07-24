package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class FavoritesList {
    @Id
    @GeneratedValue
    private UUID favoritesListId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"productName","productDescription","model","active","weight","length","width","height","createdBy","updatedBy","categories","brands","createdAt", "updatedAt"})
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "user_id" )
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts userId;

    private LocalDateTime createdAt;

    @PrePersist
    public void ensureOrderStatus() {
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getFavoritesListId() {
        return favoritesListId;
    }

    public void setFavoritesListId(UUID favoritesListId) {
        this.favoritesListId = favoritesListId;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public UserAccounts getUserId() {
        return userId;
    }

    public void setUserId(UserAccounts userId) {
        this.userId = userId;
    }
}
