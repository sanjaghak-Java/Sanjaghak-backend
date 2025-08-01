package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.Specification.OrderItemSpecifications;
import com.example.Sanjaghak.Specification.OrderSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;


    public OrderItem addOrderItem(OrderItem orderItem,UUID variantId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));

        Optional<Orders> order = ordersRepository.findByCustomerIdAndOrderStatus(customer, OrderStatus.pending);

        Orders orders = ordersRepository.findById(order.get().getOrderId()).orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد!"));

        if (order.isEmpty()) {
            throw new RuntimeException("هیچ سفارشی با وضعیت PENDING برای این خریدار یافت نشد");
        }

        if(!ordersRepository.existsById(order.get().getOrderId())) {
            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد");
        }

        if(!productVariantsRepository.existsById(variantId)) {
            throw new IllegalArgumentException("کالای مورد نظر یافت نشد");
        }

        ProductVariants products = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        if(orderItem.getQuantity() == 0){
            throw new IllegalArgumentException("تعداد کالا نباید صفر باشد");
        }

        if(orderItemRepository.existsByOrderIdAndVariantId(order.get(),products)){
            throw new IllegalArgumentException("از این کالا قبلا به سبد خرید شما اضافه شده است");
        }

        Integer stock = inventoryStockRepository.getTotalStockByVariantId(variantId);

        if(stock < orderItem.getQuantity()){
            throw new IllegalArgumentException("موجودی ناکافی است !");
        }

        orderItem.setOrderId(order.get());
        orderItem.setVariantId(products);
        orderItem.setCreatedAt(LocalDateTime.now());
        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(UUID orderItemId,OrderItem updatedOrderItem, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));

        Optional<Orders> order = ordersRepository.findByCustomerIdAndOrderStatus(customer, OrderStatus.pending);

        Orders orders = ordersRepository.findById(order.get().getOrderId()).orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد!"));

        if (order.isEmpty()) {
            throw new RuntimeException("هیچ سفارشی با وضعیت PENDING برای این خریدار یافت نشد");
        }

        if(!ordersRepository.existsById(order.get().getOrderId())) {
            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد");
        }

        if(!orders.getOrderStatus().equals(OrderStatus.pending)){
            throw new IllegalArgumentException("شما اجازه ویرایش اطلاعات این سفارش را ندارید");
        }

        OrderItem exisit = orderItemRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("ایتم سفارش مورد نظر پیدا نشد!"));

        if(updatedOrderItem.getQuantity() == 0) {
            throw new IllegalArgumentException("تعداد کالا نباید صفر باشد");
        }

        Integer stock = inventoryStockRepository.getTotalStockByVariantId(exisit.getVariantId().getVariantId());

        if(stock < updatedOrderItem.getQuantity()){
            throw new IllegalArgumentException("موجودی ناکافی است !");
        }

        exisit.setQuantity(updatedOrderItem.getQuantity());
        return orderItemRepository.save(exisit);
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


        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("ایتم سفارش مورد نظر یافت نشد.");
        }

        OrderItem delete = orderItemRepository.findById(orderItemId).orElseThrow((() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !")));

        Orders orders = ordersRepository.findById(delete.getOrderId().getOrderId()).orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد!"));

        if(!orders.getOrderStatus().equals(OrderStatus.pending)){
            throw new IllegalArgumentException("شما اجازه حذف ایتم از سفارشی که در سبد خرید شما نیست را ندارید!");
        }

        orderItemRepository.delete(delete);
    }


}
