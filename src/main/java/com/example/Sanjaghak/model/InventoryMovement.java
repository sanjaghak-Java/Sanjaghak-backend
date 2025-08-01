package com.example.Sanjaghak.model;

import com.example.Sanjaghak.Enum.MovementType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class InventoryMovement {
    @Id
    @GeneratedValue
    private UUID inventoryMovementId;

    @ManyToOne
    @JoinColumn(name = "variants_id", nullable = false)
    @JsonIgnoreProperties({"sku","price","costPrice","color","active","hexadecimal","productId" ,"createdAt","updatedAt" })
    private ProductVariants variantsId;

    @ManyToOne
    @JoinColumn(name = "from_shelf_id",nullable = true)
    @JsonIgnoreProperties(
            value = {"shelvesCode", "return", "active", "sectionsId", "userId", "createdAt"},
            allowSetters = true
    )
    private Shelves fromShelvesId;

    @ManyToOne
    @JoinColumn(name = "to_shelf_id",nullable = true)
    @JsonIgnoreProperties(
            value = {"shelvesCode", "return", "active", "sectionsId", "userId", "createdAt"},
            allowSetters = true
    )
    private Shelves toShelvesId;

    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    private LocalDateTime createdAt;

    private UUID refrenceId;

    @ManyToOne
    @JoinColumn(name = "created_by" )
    @JsonIgnoreProperties({"firstName", "lastName", "email", "phoneNumber", "role", "lastLogin", "createdAt", "updatedAt", "active"})
    private UserAccounts createdBy;

    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    @JsonIgnoreProperties(
            value = {"name","address","city","state","country","postalCode","phone","isCentral","isActive","createdAt"},
            allowSetters = true
    )
    private Warehouse fromWarehouseId;

    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    @JsonIgnoreProperties(
            value = {"name","address","city","state","country","postalCode","phone","isCentral","isActive","createdAt"},
            allowSetters = true
    )
    private Warehouse toWarehouseId;

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

    public UserAccounts getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccounts createdBy) {
        this.createdBy = createdBy;
    }

    public Shelves getFromShelvesId() {
        return fromShelvesId;
    }

    public void setFromShelvesId(Shelves fromShelvesId) {
        this.fromShelvesId = fromShelvesId;
    }

    public UUID getInventoryMovementId() {
        return inventoryMovementId;
    }

    public void setInventoryMovementId(UUID inventoryMovementId) {
        this.inventoryMovementId = inventoryMovementId;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public UUID getRefrenceId() {
        return refrenceId;
    }

    public void setRefrenceId(UUID refrenceId) {
        this.refrenceId = refrenceId;
    }

    public Shelves getToShelvesId() {
        return toShelvesId;
    }

    public void setToShelvesId(Shelves toShelvesId) {
        this.toShelvesId = toShelvesId;
    }

    public ProductVariants getVariantsId() {
        return variantsId;
    }

    public void setVariantsId(ProductVariants variantsId) {
        this.variantsId = variantsId;
    }

    public Warehouse getFromWarehouseId() {
        return fromWarehouseId;
    }

    public void setFromWarehouseId(Warehouse fromWarehouseId) {
        this.fromWarehouseId = fromWarehouseId;
    }

    public Warehouse getToWarehouseId() {
        return toWarehouseId;
    }

    public void setToWarehouseId(Warehouse toWarehouseId) {
        this.toWarehouseId = toWarehouseId;
    }
}
