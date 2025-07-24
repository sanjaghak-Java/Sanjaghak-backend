package com.example.Sanjaghak.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Suppliers {
    @Id
    @GeneratedValue
    private UUID suppliersId;

    @Column(nullable = false)
    private String supplierName;

    private String supplierEmail;

    private String supplierPhone;

    private String supplierAddress;

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

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public UUID getSuppliersId() {
        return suppliersId;
    }

    public void setSuppliersId(UUID suppliersId) {
        this.suppliersId = suppliersId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }
}
