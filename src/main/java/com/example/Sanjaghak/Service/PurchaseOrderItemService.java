package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.Statuses;
import com.example.Sanjaghak.Repository.ProductVariantsRepository;
import com.example.Sanjaghak.Repository.PurchaseOrderItemsRepository;
import com.example.Sanjaghak.Repository.PurchaseOrdersRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderItemService {
    @Autowired
    private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

    @Autowired
    private PurchaseOrdersRepository purchaseOrdersRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    public PurchaseOrderItems save(PurchaseOrderItems purchaseOrderItems, UUID purchaseOrderId, UUID variants, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        PurchaseOrders purchaseOrders = purchaseOrdersRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));

        ProductVariants productVariants = productVariantsRepository.findById(variants)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        if (!purchaseOrders.getStatus().equals(Statuses.Pending)) {
            throw new RuntimeException("شما نمی توانید به سفارشی که درحال پردازش نیست ایتم اضافه کنید !");
        }

        if (purchaseOrderItemsRepository.existsByPurchaseOrdersIdAndVariantsId(purchaseOrders, productVariants)) {
            throw new RuntimeException("این محصول در سبد خرید شما وجود دارد.");
        }

        BigDecimal unitPrice = productVariants.getCostPrice();
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(purchaseOrderItems.getQuantityOrdered()));

        BigDecimal currentSubTotal = purchaseOrders.getSubTotal() != null ? purchaseOrders.getSubTotal() : BigDecimal.ZERO;
        BigDecimal newSubTotal = currentSubTotal.add(itemTotal);
        purchaseOrders.setSubTotal(newSubTotal);

        BigDecimal shippingCost = purchaseOrders.getShippingCost() != null ? purchaseOrders.getShippingCost() : BigDecimal.ZERO;

        BigDecimal currentTaxAmount = purchaseOrders.getTaxAmount() != null ? purchaseOrders.getTaxAmount() : BigDecimal.ZERO;

        BigDecimal taxAmount = (shippingCost.add(newSubTotal)).multiply(BigDecimal.valueOf(0.02));
        purchaseOrders.setTaxAmount(taxAmount);

        BigDecimal totalAmount = newSubTotal.add(taxAmount).add(shippingCost);
        purchaseOrders.setTotalAmount(totalAmount);

        purchaseOrdersRepository.save(purchaseOrders);

        purchaseOrderItems.setPurchaseOrdersId(purchaseOrders);
        purchaseOrderItems.setVariantsId(productVariants);
        return purchaseOrderItemsRepository.save(purchaseOrderItems);
    }



    public PurchaseOrderItems updatePurchaseOrderItems(UUID purchaseOrderItemsId, PurchaseOrderItems updatedPurchaseOrderItems, UUID purchaseOrderId, UUID variants, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        PurchaseOrders purchaseOrders = purchaseOrdersRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));

        ProductVariants newVariant = productVariantsRepository.findById(variants)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        if (!purchaseOrders.getStatus().equals(Statuses.Pending)) {
            throw new RuntimeException("شما نمی‌توانید سفارشی که در حال پردازش نیست را ویرایش کنید !");
        }

        PurchaseOrderItems existing = purchaseOrderItemsRepository.findById(purchaseOrderItemsId)
                .orElseThrow(() -> new EntityNotFoundException("آیتم سفارش مورد نظر پیدا نشد !"));

        BigDecimal oldItemTotal = existing.getVariantsId().getCostPrice()
                .multiply(BigDecimal.valueOf(existing.getQuantityOrdered()));

        if (!existing.getVariantsId().getVariantId().equals(variants)) {
            if (purchaseOrderItemsRepository.existsByPurchaseOrdersIdAndVariantsId(purchaseOrders, newVariant)) {
                throw new RuntimeException("این محصول در سبد خرید شما وجود دارد.");
            }
            existing.setVariantsId(newVariant);
        }

        existing.setQuantityOrdered(updatedPurchaseOrderItems.getQuantityOrdered());

        BigDecimal newItemTotal = newVariant.getCostPrice()
                .multiply(BigDecimal.valueOf(updatedPurchaseOrderItems.getQuantityOrdered()));

        BigDecimal currentSubTotal = purchaseOrders.getSubTotal() != null ? purchaseOrders.getSubTotal() : BigDecimal.ZERO;
        BigDecimal newSubTotal = currentSubTotal.subtract(oldItemTotal).add(newItemTotal);
        purchaseOrders.setSubTotal(newSubTotal);

        BigDecimal shippingCost = purchaseOrders.getShippingCost() != null ? purchaseOrders.getShippingCost() : BigDecimal.ZERO;

        BigDecimal taxAmount = (newSubTotal.add(shippingCost)).multiply(BigDecimal.valueOf(0.02));
        purchaseOrders.setTaxAmount(taxAmount);

        BigDecimal totalAmount = newSubTotal.add(shippingCost).add(taxAmount);
        purchaseOrders.setTotalAmount(totalAmount);

        purchaseOrdersRepository.save(purchaseOrders);
        return purchaseOrderItemsRepository.save(existing);
    }


    public PurchaseOrderItems getPurchaseOrdersItemById(UUID purchaseOrderItemsId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return purchaseOrderItemsRepository.findById(purchaseOrderItemsId).orElseThrow(()-> new EntityNotFoundException("ایتم سفارش مورد نظر پیدا نشد !"));
    }

    public List<PurchaseOrderItems> getAllPurchaseOrdersItem(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return purchaseOrderItemsRepository.findAll();
    }

    public List<PurchaseOrderItems> getPurchaseOrdersItemsByorderId(UUID orderId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return purchaseOrderItemsRepository.findByPurchaseOrdersId_PurchaseOrdersId(orderId);

    }

    public void deletePurchaseOrderItems(UUID purchaseOrderItemsId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        PurchaseOrderItems item = purchaseOrderItemsRepository.findById(purchaseOrderItemsId)
                .orElseThrow(() -> new EntityNotFoundException("آیتم سفارش مورد نظر پیدا نشد !"));

        PurchaseOrders order = item.getPurchaseOrdersId();

        if (!order.getStatus().equals(Statuses.Pending)) {
            throw new RuntimeException("شما اجازه حذف جزئیات سفارش در حالت غیر پردازش را ندارید!");
        }

        BigDecimal itemTotal = item.getVariantsId().getCostPrice()
                .multiply(BigDecimal.valueOf(item.getQuantityOrdered()));

        BigDecimal currentSubTotal = order.getSubTotal() != null ? order.getSubTotal() : BigDecimal.ZERO;
        BigDecimal newSubTotal = currentSubTotal.subtract(itemTotal).max(BigDecimal.ZERO); // از صفر کمتر نره
        order.setSubTotal(newSubTotal);

        BigDecimal shippingCost = order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO;

        BigDecimal taxAmount = (newSubTotal.add(shippingCost)).multiply(BigDecimal.valueOf(0.02));
        order.setTaxAmount(taxAmount);

        BigDecimal totalAmount = newSubTotal.add(shippingCost).add(taxAmount);
        order.setTotalAmount(totalAmount);

        purchaseOrdersRepository.save(order);

        purchaseOrderItemsRepository.delete(item);
    }

}
