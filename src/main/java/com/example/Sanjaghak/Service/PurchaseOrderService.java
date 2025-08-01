package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.Statuses;
import com.example.Sanjaghak.Repository.PurchaseOrdersRepository;
import com.example.Sanjaghak.Repository.SuppliersRepository;
import com.example.Sanjaghak.Repository.WarehouseRepository;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseOrdersRepository purchaseOrdersRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SuppliersRepository suppliersRepository;

    public PurchaseOrders createPurchaseOrder(PurchaseOrders purchaseOrders ,UUID warehouseId, UUID suppliersId ,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("انبار مورد نظر پیدا نشد !"));

        Suppliers suppliers = suppliersRepository.findById(suppliersId)
                .orElseThrow(() -> new RuntimeException("تامیین کننده مورد نظر پیدا نشد !"));


        if (purchaseOrders.getExpectedDate() != null && purchaseOrders.getOrderDate() != null) {
            if (purchaseOrders.getExpectedDate().isBefore(purchaseOrders.getOrderDate().toLocalDate())) {
                throw new RuntimeException("تاریخ رسیدن نمی‌تواند قبل از تاریخ سفارش باشد.");
            }
        }

        PurchaseOrders order = new PurchaseOrders();
        order.setPurchaseOrdersId(purchaseOrders.getPurchaseOrdersId());
        order.setOrderDate(purchaseOrders.getOrderDate());
        order.setExpectedDate(purchaseOrders.getExpectedDate());
        order.setWarehouseId(warehouse);
        order.setSuppliersId(suppliers);
        order.setShippingCost(purchaseOrders.getShippingCost());
        order.setTotalAmount(purchaseOrders.getShippingCost());
        order.setOrderNumber(generateUniqueOrderNumber());

        return purchaseOrdersRepository.save(order);
    }

    public PurchaseOrders updatePurchaseOrders(UUID purchaseOrdersId, PurchaseOrders updatedPurchaseOrders, UUID warehouseId,UUID suppliersId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("انبار مورد نظر پیدا نشد !"));

        Suppliers suppliers = suppliersRepository.findById(suppliersId)
                .orElseThrow(() -> new RuntimeException("تامیین کننده مورد نظر پیدا نشد !"));


        PurchaseOrders existing = purchaseOrdersRepository.findById(purchaseOrdersId)
                .orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد"));

        if(!existing.getStatus().equals(Statuses.Pending) && !existing.getStatus().equals(Statuses.Shipping)) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (updatedPurchaseOrders.getExpectedDate() != null && existing.getOrderDate() != null) {
            if (updatedPurchaseOrders.getExpectedDate().isBefore(existing.getOrderDate().toLocalDate())) {
                throw new RuntimeException("تاریخ رسیدن نمی‌تواند قبل از تاریخ سفارش باشد.");
            }
        }

        existing.setShippingCost(updatedPurchaseOrders.getShippingCost());
        existing.setTotalAmount(
                safe(updatedPurchaseOrders.getShippingCost())
                        .add(safe(updatedPurchaseOrders.getSubTotal()))
                        .add(safe(updatedPurchaseOrders.getTaxAmount()))
        );

        existing.setWarehouseId(warehouse);
        existing.setSuppliersId(suppliers);
        existing.setExpectedDate(updatedPurchaseOrders.getExpectedDate());

        return purchaseOrdersRepository.save(existing);
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public PurchaseOrders getPurchaseOrdersById(UUID purchaseOrdersId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return purchaseOrdersRepository.findById(purchaseOrdersId).orElseThrow(()-> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));
    }

    public List<PurchaseOrders> getAllPurchaseOrders(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return purchaseOrdersRepository.findAll();
    }

    public void cancelPurchaseOrder(UUID purchaseOrderId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        PurchaseOrders order = purchaseOrdersRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("سفارشی با این شناسه یافت نشد."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedDateTime = order.getExpectedDate().atStartOfDay().minusDays(1);

        if (now.isBefore(order.getOrderDate()) || now.isAfter(expectedDateTime)) {
            throw new RuntimeException("امکان لغو این سفارش در بازه زمانی فعلی وجود ندارد.");
        }

        order.setStatus(Statuses.cancelled);
        purchaseOrdersRepository.save(order);
    }

    private String generateUniqueOrderNumber() {
        String orderNumber;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        do {
            String datePart = LocalDate.now().format(formatter);
            int randomPart = (int) (Math.random() * 9000) + 1000; // عدد ۴ رقمی بین 1000 تا 9999
            orderNumber = "P-ORD-" + datePart + "-" + randomPart;
        } while (purchaseOrdersRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }

    public void registratPurchaseOrder(UUID purchaseOrderId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        PurchaseOrders order = purchaseOrdersRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("سفارشی با این شناسه یافت نشد."));

        if(!order.getStatus().equals(Statuses.Pending)) {
            throw new RuntimeException("نمی توان سفارشی که در حال پردازش نیست را نهایی کرد!");
        }

        order.setStatus(Statuses.Shipping);
        purchaseOrdersRepository.save(order);
    }




}
