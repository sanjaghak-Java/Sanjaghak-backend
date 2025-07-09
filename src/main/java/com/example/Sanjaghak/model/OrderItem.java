package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"customerId","orderNumber","orderStatus","paymentMethod","paymentStatus","billingAddressId","subTotal","shippingCost","taxAmount","discountAmount","totalAmount","notes", "createdAt", "updatedAt"})
    private Orders orderId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"productName","productDescription","sku","model","price","costPrice","active","weight","length","width","height","createdBy","updatedBy","categories","brands","createdAt", "updatedAt"})
    private Products productId;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @PrePersist
    public void ensureOrderStatus() {
        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
