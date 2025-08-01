package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.OrderItemService;
import com.example.Sanjaghak.model.OrderItem;
import com.example.Sanjaghak.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @PostMapping("/orderItemRegistration")
    public ResponseEntity<?> orderItemRegistration(@RequestBody OrderItem orderItem,
                                                   @RequestParam UUID productId,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            OrderItem save = orderItemService.addOrderItem(orderItem,productId, token);
            return ResponseEntity.ok(save);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable UUID id,
                                             @RequestBody OrderItem orderItem,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            OrderItem update = orderItemService.updateOrderItem(id, orderItem, token);
            return ResponseEntity.ok(update);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(orderItemService.getOrderItemById(id));
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

    @GetMapping("/getOrderItemByFilter")
    public Page<OrderItem> getOrderItemByFilter(
            @RequestParam(required = false) BigDecimal minTotalAmount,
            @RequestParam(required = false) BigDecimal maxTotalAmount,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) UUID productId,
            Pageable pageable) {
        return orderItemService.findOrderItemsByfilter(
                minTotalAmount,
                maxTotalAmount,
                orderId,
                productId,
                pageable
        ) ;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            orderItemService.deleteOrderItem(id, token);
            return ResponseEntity.ok().body("ایتم سفارش مورد نظر با موفقیت حذف شد");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));

        }
    }
}
