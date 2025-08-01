package com.example.Sanjaghak.model;

import com.example.Sanjaghak.Enum.Statuses;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class PurchaseOrders {
    @Id
    @GeneratedValue
    private UUID purchaseOrdersId;

    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statuses status;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonIgnoreProperties({"name","address","city","state","country","postalCode","phone","isCentral","isActive","createdAt"})
    private Warehouse warehouseId;

    @ManyToOne
    @JoinColumn(name = "suppliers_id", nullable = false)
    @JsonIgnoreProperties({"supplierName","supplierEmail","supplierPhone","supplierAddress","city","state","country","postalCode","createdAt","updatedAt"})
    private Suppliers suppliersId;

    private BigDecimal subTotal;

    private BigDecimal shippingCost;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    private LocalDate expectedDate;

    @PrePersist
    public void ensureOrderStatus() {
        if(orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if(expectedDate == null) {
            expectedDate = LocalDate.now();
        }
        if(status == null) {
            status = Statuses.Pending;
        }
        if(subTotal == null) {
            subTotal = BigDecimal.valueOf(0);
        }
        if(shippingCost == null) {
            shippingCost = BigDecimal.valueOf(0);
        }
        if(taxAmount == null) {
            taxAmount = BigDecimal.valueOf(0);
        }
        if(totalAmount == null) {
            totalAmount = BigDecimal.valueOf(0);
        }
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public UUID getPurchaseOrdersId() {
        return purchaseOrdersId;
    }

    public void setPurchaseOrdersId(UUID purchaseOrdersId) {
        this.purchaseOrdersId = purchaseOrdersId;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Suppliers getSuppliersId() {
        return suppliersId;
    }

    public void setSuppliersId(Suppliers suppliersId) {
        this.suppliersId = suppliersId;
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

    public Warehouse getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Warehouse warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
