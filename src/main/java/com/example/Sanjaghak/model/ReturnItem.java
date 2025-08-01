package com.example.Sanjaghak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ReturnItem {

    @Id
    @GeneratedValue
    private UUID returnItemId;

    @ManyToOne
    @JoinColumn(name = "return_id", nullable = false)
    @JsonIgnoreProperties({"orderId","returnNumber","returnStatus","createdAt"})
    private Return returnId;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    @JsonIgnoreProperties({"orderId","variantId","quantity","unitPrice","subTotal","taxAmount","discountAmount","totalAmount","createdAt"})
    private OrderItem orderItemId;

    private int quantity;

    private String title;

    private String description;

    private boolean restock;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrderItem getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItem orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isRestock() {
        return restock;
    }

    public void setRestock(boolean restock) {
        this.restock = restock;
    }

    public Return getReturnId() {
        return returnId;
    }

    public void setReturnId(Return returnId) {
        this.returnId = returnId;
    }

    public UUID getReturnItemId() {
        return returnItemId;
    }

    public void setReturnItemId(UUID returnItemId) {
        this.returnItemId = returnItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
