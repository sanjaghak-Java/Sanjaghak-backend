package com.example.Sanjaghak.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import java.util.UUID;



@Entity
public class ProductAttribute {
    @Id
    @GeneratedValue
    private UUID attributeId;

    private String attributeName;

    private String attributeType;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({ "firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    public UUID getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(UUID attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
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

}
