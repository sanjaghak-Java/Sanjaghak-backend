package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class InventoryStock {
    @Id
    @GeneratedValue
    private UUID inventoryStockId;

    private int quantityOnHand;

    private int reservedInventory;

    private int minimumLevel;

    private int maximumLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by" )
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by" )
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts updatedBy;

    @ManyToOne
    @JoinColumn(name = "variants_id", nullable = false)
    @JsonIgnoreProperties({"sku","price","costPrice","color","active","hexadecimal","productId" ,"createdAt","updatedAt" })
    private ProductVariants variantsId;

    @ManyToOne
    @JoinColumn(name = "shelves_id", nullable = false)
    @JsonIgnoreProperties({"shelvesCode","return","active","sectionsId","userId","createdAt"})
    private Shelves shelvesId;

    private Boolean isActive;

    @PrePersist
    public void ensureOrderStatus() {
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if(isActive == null) {
            isActive = true;
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

    public UserAccounts getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccounts createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getInventoryStockId() {
        return inventoryStockId;
    }

    public void setInventoryStockId(UUID inventoryStockId) {
        this.inventoryStockId = inventoryStockId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getMaximumLevel() {
        return maximumLevel;
    }

    public void setMaximumLevel(int maximumLevel) {
        this.maximumLevel = maximumLevel;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(int minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public int getReservedInventory() {
        return reservedInventory;
    }

    public void setReservedInventory(int reservedInventory) {
        this.reservedInventory = reservedInventory;
    }

    public Shelves getShelvesId() {
        return shelvesId;
    }

    public void setShelvesId(Shelves shelvesId) {
        this.shelvesId = shelvesId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserAccounts getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserAccounts updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ProductVariants getVariantsId() {
        return variantsId;
    }

    public void setVariantsId(ProductVariants variantsId) {
        this.variantsId = variantsId;
    }

}
