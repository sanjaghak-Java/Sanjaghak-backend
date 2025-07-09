package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CustomerAddressRepository;
import com.example.Sanjaghak.Repository.CustomerRepository;
import com.example.Sanjaghak.Repository.OrdersRepository;
import com.example.Sanjaghak.Specification.OrderSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Orders addOrder(Orders order, UUID billingAddressId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if(!customerAddressRepository.existsById(billingAddressId)) {
            throw new IllegalArgumentException("آدرس مورد نظر یافت نشد");
        }

        CustomerAddress customerAddress = customerAddressRepository.findById(billingAddressId)
                .orElseThrow(() -> new RuntimeException("آدرس پیدا نشد"));

        if(!customerRepository.existsByUserId_Id(userId)) {
            throw new IllegalArgumentException("خریدار مورد نظر یافت نشد");
        }
        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));


        order.setCustomerId(customer);
        order.setBillingAddressId(customerAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderNumber(generateUniqueOrderNumber());

        return ordersRepository.save(order);
    }

    private String generateUniqueOrderNumber() {
        String orderNumber;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        do {
            String datePart = LocalDate.now().format(formatter);
            int randomPart = (int) (Math.random() * 9000) + 1000; // عدد ۴ رقمی بین 1000 تا 9999
            orderNumber = "ORD-" + datePart + "-" + randomPart;
        } while (ordersRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }

    public Orders updateOrder(UUID orderId,Orders updatedOrder, UUID billingAddressId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

//        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
//            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
//        }

        if(!customerAddressRepository.existsById(billingAddressId)) {
            throw new IllegalArgumentException("آدرس مورد نظر یافت نشد");
        }

        CustomerAddress customerAddress = customerAddressRepository.findById(billingAddressId)
                .orElseThrow(() -> new RuntimeException("آدرس پیدا نشد"));

        if(!customerRepository.existsByUserId_Id(userId)) {
            throw new IllegalArgumentException("خریدار مورد نظر یافت نشد");
        }
        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));


        Orders existing = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد"));


        existing.setBillingAddressId(customerAddress);
        existing.setNotes(updatedOrder.getNotes());
        existing.setDiscountAmount(updatedOrder.getDiscountAmount());
        existing.setShippingCost(updatedOrder.getShippingCost());
        existing.setTotalAmount(updatedOrder.getTotalAmount());
        existing.setTaxAmount(updatedOrder.getTaxAmount());
        existing.setSubTotal(updatedOrder.getSubTotal());
        existing.setUpdatedAt(LocalDateTime.now());

        return ordersRepository.save(existing);
    }

    public Orders getOrderById(UUID orderId) {
        return ordersRepository.findById(orderId).orElseThrow(()-> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));
    }

    public Page<Orders> findOrdersByfilter(
            BigDecimal minTotalAmount,
            BigDecimal maxTotalAmount,
            UUID customerId,
            String orderNumber,
            UUID billingAddressId,
            Pageable pageable) {
        return ordersRepository.findAll(
                OrderSpecifications.filterOrders(
                        minTotalAmount,
                        maxTotalAmount,
                        customerId,
                        orderNumber,
                        billingAddressId
                ),
                pageable
        );
    }

    public void deleteOrder(UUID orderId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!ordersRepository.existsById(orderId)) {
            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد.");
        }
        Orders delete = ordersRepository.findById(orderId).orElseThrow((() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !")));
        ordersRepository.delete(delete);
    }

}
