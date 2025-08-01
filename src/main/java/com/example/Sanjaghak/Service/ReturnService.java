package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.MovementType;
import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.Enum.ReturnStatus;
import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;


    public Return create(UUID orderId,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("سفارش پیدا نشد"));

        if(!order.getOrderStatus().equals(OrderStatus.delivered)){
            throw new RuntimeException("شما نمی توانید به این سفارش درخواست مرجوعی بدهید ");
        }

        LocalDateTime now = LocalDateTime.now();

        if (order.getCreatedAt().isBefore(now.minusDays(10))) {
            throw new IllegalStateException("بیش از 10 روز از ایجاد این رکورد گذشته است");
        }

        Return returnObj = new Return();
        returnObj.setOrderId(order);
        returnObj.setCreatedAt(LocalDateTime.now());
        returnObj.setReturnStatus(ReturnStatus.PENDING);
        return returnRepository.save(returnObj);
    }

    public Return getReturnById(UUID ReturnId) {
        return returnRepository.findById(ReturnId).orElseThrow(()-> new EntityNotFoundException("درخواست مرجوعی مورد نظر پیدا نشد !"));
    }

    public List<Return> getAllReturn() {
        return returnRepository.findAll();
    }

    public List<Return> getAllReturnByUserId(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        List<Orders> ordersList = ordersRepository.findAllByCustomerIdAndOrderStatus(customer, OrderStatus.delivered);

        return returnRepository.findByOrderIdIn(ordersList);
    }

    public void deleteReturnById(UUID ReturnId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Return returnObj = getReturnById(ReturnId);

        if(!returnObj.getReturnStatus().equals(ReturnStatus.PENDING)){
            throw new RuntimeException("امکان حذف وجود ندارد");
        }

        List<ReturnItem> returnItems = returnItemRepository.findByReturnId(returnObj);

        returnItemRepository.deleteAll(returnItems);
        returnRepository.deleteById(ReturnId);
    }

    public Return finalizeReturn(UUID ReturnId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Return returnObj = getReturnById(ReturnId);

        Orders order = ordersRepository.findById(returnObj.getOrderId().getOrderId())
                .orElseThrow(() -> new RuntimeException("سفارش پیدا نشد"));

        if(!order.getOrderStatus().equals(OrderStatus.delivered)){
            throw new RuntimeException("شما نمی توانید به این سفارش درخواست مرجوعی بدهید ");
        }

        LocalDateTime now = LocalDateTime.now();

        if (order.getCreatedAt().isBefore(now.minusDays(10))) {
            throw new IllegalStateException("بیش از 10 روز از ایجاد این رکورد گذشته است");
        }

        returnObj.setReturnStatus(ReturnStatus.CHECKING);
        returnObj.setReturnNumber(generateUniqueReturnNumber());
        returnObj.setCreatedAt(LocalDateTime.now());
        return returnRepository.save(returnObj);
    }

    private String generateUniqueReturnNumber() {
        String orderNumber;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        do {
            String datePart = LocalDate.now().format(formatter);
            int randomPart = (int) (Math.random() * 9000) + 1000;
            orderNumber = "RET-ORD-" + datePart + "-" + randomPart;
        } while (ordersRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }

    public List<Return> findByReturnStatus(ReturnStatus returnStatus, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        List<Return> returnList = returnRepository.findByReturnStatus(returnStatus);
        return returnList;
    }

    public Return checked(UUID returnId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Return returnObj = getReturnById(returnId);

        if (!returnObj.getReturnStatus().equals(ReturnStatus.CHECKING)) {
            throw new RuntimeException("شما نمی توانید این درخواست مرجوعی را با این وضعیت برسی کنید !");
        }

        List<ReturnItem> returnItems = returnItemRepository.findByReturnIdAndRestockTrue(returnObj);

        Warehouse centralWarehouse = warehouseRepository.findByIsCentralTrueAndIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("هیچ انباری مرکزی فعال یافت نشد"));

        UserAccounts createdBy = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        for (ReturnItem returnItem : returnItems) {
            ProductVariants variant = returnItem.getOrderItemId().getVariantId();

            InventoryMovement inventoryMovement = new InventoryMovement();
            inventoryMovement.setVariantsId(variant);
            inventoryMovement.setQuantity(returnItem.getQuantity());
            inventoryMovement.setMovementType(MovementType.SALE_RETURN_REQUEST);
            inventoryMovement.setRefrenceId(returnId);
            inventoryMovement.setCreatedBy(createdBy);
            inventoryMovement.setCreatedAt(LocalDateTime.now());
            inventoryMovement.setToWarehouseId(centralWarehouse);

            inventoryMovementRepository.save(inventoryMovement);
        }

        returnObj.setReturnStatus(ReturnStatus.CHECKED);
        return returnRepository.save(returnObj);
    }

    public List<Return> getAllReturnsByStatusCheck(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        List<Return> returnCheckedList = returnRepository.findByReturnStatus(ReturnStatus.CHECKED);

        return returnCheckedList.stream()
                .filter(r -> returnItemRepository.existsByReturnIdAndRestockTrue(r))
                .collect(Collectors.toList());
    }


    public void cancelReturn (UUID returnId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Return returnObj = getReturnById(returnId);

        if(!returnObj.getReturnStatus().equals(ReturnStatus.CHECKING)) {
            throw new RuntimeException("زمان لغو درخواست مرجوعی گذشته است !");
        }
        returnObj.setReturnStatus(ReturnStatus.CANCELED);
        returnRepository.save(returnObj);
    }




}
