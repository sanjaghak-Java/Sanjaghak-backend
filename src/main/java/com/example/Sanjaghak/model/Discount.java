package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Discount {
    @Id
    @GeneratedValue
    private UUID discountId;

    private int discountPercentage;

    private Boolean isActive;

    private LocalDateTime startFrom;

    private LocalDateTime endFrom;

    private String discountDescription;

    @ManyToOne
    @JoinColumn(name = "variants_id", nullable = false)
    @JsonIgnoreProperties({"sku","price","costPrice","color","active","hexadecimal","productId" ,"createdAt","updatedAt" })
    private ProductVariants variantsId;

    @PrePersist
    public void ensureOrderStatus() {
        if(startFrom == null) {
            startFrom = LocalDateTime.now();
        }
        if(endFrom == null) {
            endFrom = LocalDateTime.now();
        }
        if(isActive == null) {
            isActive = true;
        }
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getEndFrom() {
        return endFrom;
    }

    public void setEndFrom(LocalDateTime endFrom) {
        this.endFrom = endFrom;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(LocalDateTime startFrom) {
        this.startFrom = startFrom;
    }

    public ProductVariants getVariantsId() {
        return variantsId;
    }

    public void setVariantsId(ProductVariants variantsId) {
        this.variantsId = variantsId;
    }
}
