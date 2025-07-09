package com.example.Sanjaghak.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private UUID customerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccounts userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureOrderStatus() {
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if(updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserAccounts getUserId() {
        return userId;
    }

    public void setUserId(UserAccounts userId) {
        this.userId = userId;
    }
}
