package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.OrderItemRepository;
import com.example.Sanjaghak.Repository.OrdersRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.ProductVariantsRepository;
import com.example.Sanjaghak.Specification.OrderItemSpecifications;
import com.example.Sanjaghak.Specification.OrderSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    public OrderItem addOrderItem(OrderItem orderItem, UUID orderId, UUID variantId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if(!ordersRepository.existsById(orderId)) {
            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد");
        }

        if(!productVariantsRepository.existsById(variantId)) {
            throw new IllegalArgumentException("کالای مورد نظر یافت نشد");
        }

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("سفارش پیدا نشد"));

        ProductVariants products = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        if(orderItem.getQuantity() == 0){
            throw new IllegalArgumentException("تعداد کالا نباید صفر باشد");
        }

        if(orderItem.getUnitPrice() == null){
            throw new IllegalArgumentException("قیمت کالا نباید خالی باشد");
        }

        if(orderItem.getSubTotal() == null){
            throw new IllegalArgumentException("مجموع قیمت کالا نباید خالی باشد");
        }

        if(orderItem.getTotalAmount() == null){
            throw new IllegalArgumentException("قیمت نهایی نباید خالی باشد");
        }

        orderItem.setOrderId(orders);
        orderItem.setVariantId(products);
        orderItem.setCreatedAt(LocalDateTime.now());

        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(UUID orderItemId,OrderItem updatedOrderItem, UUID orderId,UUID variantId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

//        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
//            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
//        }

        if(!ordersRepository.existsById(orderId)) {
            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد");
        }

        if(!productVariantsRepository.existsById(variantId)) {
            throw new IllegalArgumentException("کالای مورد نظر یافت نشد");
        }

        if(!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("ایتم خرید مورد نظر یافت نشد");
        }

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("سفارش پیدا نشد"));

        ProductVariants products = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        OrderItem update = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("ایتم خرید مورد نظر یافت نشد"));

        if(updatedOrderItem.getQuantity() == 0){
            throw new IllegalArgumentException("تعداد کالا نباید صفر باشد");
        }

        if(updatedOrderItem.getUnitPrice() == null){
            throw new IllegalArgumentException("قیمت کالا نباید خالی باشد");
        }

        if(updatedOrderItem.getSubTotal() == null){
            throw new IllegalArgumentException("مجموع قیمت کالا نباید خالی باشد");
        }

        if(updatedOrderItem.getTotalAmount() == null){
            throw new IllegalArgumentException("قیمت نهایی نباید خالی باشد");
        }


        update.setOrderId(orders);
        update.setVariantId(products);
        update.setQuantity(updatedOrderItem.getQuantity());
        update.setUnitPrice(updatedOrderItem.getUnitPrice());
        update.setTotalAmount(updatedOrderItem.getTotalAmount());
        update.setTaxAmount(updatedOrderItem.getTaxAmount());
        update.setDiscountAmount(updatedOrderItem.getDiscountAmount());
        update.setSubTotal(updatedOrderItem.getSubTotal());

        return orderItemRepository.save(update);
    }

    public OrderItem getOrderItemById(UUID orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(()-> new EntityNotFoundException("ایتم سفارش مورد نظر پیدا نشد !"));
    }

    public Page<OrderItem> findOrderItemsByfilter(
            BigDecimal minTotalAmount,
            BigDecimal maxTotalAmount,
            UUID orderId,
            UUID productId,
            Pageable pageable) {
        return orderItemRepository.findAll(
                OrderItemSpecifications.filterOrderItems(
                        minTotalAmount,
                        maxTotalAmount,
                        orderId,
                        productId
                ),
                pageable
        );
    }

    public void deleteOrderItem(UUID orderItemId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

//        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
//            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
//        }

        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("ایتم سفارش مورد نظر یافت نشد.");
        }
        OrderItem delete = orderItemRepository.findById(orderItemId).orElseThrow((() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !")));
        orderItemRepository.delete(delete);
    }


}
