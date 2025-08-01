package com.example.Sanjaghak.model;

import com.example.Sanjaghak.Enum.ReturnStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Return {
    @Id
    @GeneratedValue
    private UUID returnId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"customerId","orderNumber","orderStatus","paymentMethod","paymentStatus","billingAddressId","subTotal","shippingCost","taxAmount","discountAmount","totalAmount","notes", "createdAt", "updatedAt"})
    private Orders orderId;

    private String returnNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus returnStatus;

    private LocalDateTime createdAt;


    @PrePersist
    public void ensureOrderStatus() {
        if (returnStatus == null) {
            returnStatus = ReturnStatus.PENDING;
        }
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

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public UUID getReturnId() {
        return returnId;
    }

    public void setReturnId(UUID returnId) {
        this.returnId = returnId;
    }

    public String getReturnNumber() {
        return returnNumber;
    }

    public void setReturnNumber(String returnNumber) {
        this.returnNumber = returnNumber;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }
}
