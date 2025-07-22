package com.example.Sanjaghak.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class ProductAttributeValue {

    @Id
    @GeneratedValue
    private UUID id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"productName", "model","productDescription","categories","brands", "sku", "price", "costPrice", "weight", "createdAt", "updatedAt", "active","length","width","height", "createdBy", "updatedBy"})
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    @JsonIgnoreProperties({ "createdAt", "updatedAt","createdBy"})
    private ProductAttribute attributeId;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    private LocalDateTime createdAt;

    public ProductAttribute getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(ProductAttribute attributeId) {
        this.attributeId = attributeId;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
