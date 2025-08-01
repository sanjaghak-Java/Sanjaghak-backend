package com.example.Sanjaghak.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class PurchaseOrderItems {
    @Id
    @GeneratedValue
    private UUID purchaseOrderItemsId;

    private int quantityOrdered;

    private int recivedQuantity;

    @ManyToOne
    @JoinColumn(name = "variants_id", nullable = false)
    @JsonIgnoreProperties({"sku","price","costPrice","color","active","hexadecimal","productId" ,"createdAt","updatedAt" })
    private ProductVariants variantsId;

    @ManyToOne
    @JoinColumn(name = "purchase_orders_id", nullable = false)
    @JsonIgnoreProperties({"status","warehouseId","suppliersId","subTotal","shippingCost","taxAmount","totalAmount" ,"orderDate","expectedDate" })
    private PurchaseOrders purchaseOrdersId;

    private BigDecimal unitPrice;

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public UUID getPurchaseOrderItemsId() {
        return purchaseOrderItemsId;
    }

    public void setPurchaseOrderItemsId(UUID purchaseOrderItemsId) {
        this.purchaseOrderItemsId = purchaseOrderItemsId;
    }

    public PurchaseOrders getPurchaseOrdersId() {
        return purchaseOrdersId;
    }

    public void setPurchaseOrdersId(PurchaseOrders purchaseOrdersId) {
        this.purchaseOrdersId = purchaseOrdersId;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public int getRecivedQuantity() {
        return recivedQuantity;
    }

    public void setRecivedQuantity(int recivedQuantity) {
        this.recivedQuantity = recivedQuantity;
    }

    public ProductVariants getVariantsId() {
        return variantsId;
    }

    public void setVariantsId(ProductVariants variantsId) {
        this.variantsId = variantsId;
    }
}
