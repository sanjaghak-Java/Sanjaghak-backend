package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Shelves {
    @Id
    @GeneratedValue
    private UUID shelvesId;

    private String shelvesCode;

    private Boolean isReturn;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "sections_id", nullable = false)
    @JsonIgnoreProperties({"name","active","warehouseId","createdAt"})
    private Sections sectionsId;

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
        if(isActive == null) {
            isActive = true;
        }
        if(isReturn == null) {
            isReturn = false;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getReturn() {
        return isReturn;
    }

    public void setReturn(Boolean aReturn) {
        isReturn = aReturn;
    }

    public Sections getSectionsId() {
        return sectionsId;
    }

    public void setSectionsId(Sections sectionsId) {
        this.sectionsId = sectionsId;
    }

    public String getShelvesCode() {
        return shelvesCode;
    }

    public void setShelvesCode(String shelvesCode) {
        this.shelvesCode = shelvesCode;
    }

    public UUID getShelvesId() {
        return shelvesId;
    }

    public void setShelvesId(UUID shelvesId) {
        this.shelvesId = shelvesId;
    }

    public UserAccounts getUserId() {
        return userId;
    }

    public void setUserId(UserAccounts userId) {
        this.userId = userId;
    }
}
