package com.example.Sanjaghak.model;

import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.Enum.PaymentMethod;
import com.example.Sanjaghak.Enum.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Orders {
    @Id
    @GeneratedValue
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"userId", "createdAt", "updatedAt"})
    private Customer customerId;

    @Column(nullable = false,unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentStatus paymentStatus;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "billing_Address_Id", nullable = false)
    @JsonIgnoreProperties({"customerId", "addressLine1", "addressLine2","city","state","country","postalCode","phone","createdAt","updatedAt"})
    private CustomerAddress billingAddressId;

    @Column(nullable = false)
    private BigDecimal subTotal;        // مجموع قیمت کالاها بدون مالیات و تخفیف

    @Column(nullable = false)
    private BigDecimal shippingCost;    // هزینه ارسال

    @Column(nullable = false)
    private BigDecimal taxAmount;       // مالیات

    @Column(nullable = false)
    private BigDecimal discountAmount;  // مقدار تخفیف

    @Column(nullable = false)
    private BigDecimal totalAmount;     // مبلغ نهایی بعد از همه موارد بالا

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureOrderStatus() {
        if (orderStatus == null) {
            orderStatus = OrderStatus.pending;
        }
//        if (paymentStatus == null) {
//            paymentStatus = PaymentStatus.pending;
//        }
//        if (paymentMethod == null) {
//            paymentMethod = PaymentMethod.bankTransfer;
//        }
        if (shippingCost == null) {
            shippingCost = BigDecimal.ZERO;
        }
        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if(updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public CustomerAddress getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(CustomerAddress billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Customer customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

//    public PaymentMethod getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public void setPaymentMethod(PaymentMethod paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }
//
//    public PaymentStatus getPaymentStatus() {
//        return paymentStatus;
//    }
//
//    public void setPaymentStatus(PaymentStatus paymentStatus) {
//        this.paymentStatus = paymentStatus;
//    }
}
