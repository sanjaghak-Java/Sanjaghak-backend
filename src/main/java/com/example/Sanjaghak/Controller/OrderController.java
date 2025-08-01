package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.OrderService;
import com.example.Sanjaghak.model.InventoryMovement;
import com.example.Sanjaghak.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/Orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

//    @PostMapping("/orderRegistration")
//    public ResponseEntity<?> orderRegistration(@RequestBody Orders order,
//                                      @RequestParam UUID billingAddressId,
//                                      @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            Orders save = orderService.addOrder(order, billingAddressId, token);
//            return ResponseEntity.ok(save);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(Map.of("error", ex.getMessage()));
//
//        } catch (RuntimeException ex) {
//            String msg = ex.getMessage();
//            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", msg));
//            }
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", ex.getMessage()));
//
//        }
//    }

    @PostMapping("/reorder/{originalOrderId}")
    public ResponseEntity<?> reorder(@PathVariable UUID originalOrderId,
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Orders newOrder = orderService.reorder(originalOrderId, token);
            return ResponseEntity.ok(newOrder);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

    @GetMapping("/processing-with-inventory")
    public ResponseEntity<List<Orders>> getProcessingOrdersWithInventory() {
        List<Orders> orders = orderService.getProcessingOrdersWithCompleteOrderMovements();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-warehouse/{warehouseId}/order-requests")
    public ResponseEntity<List<InventoryMovement>> getOrderRequestMovementsByWarehouse(
            @PathVariable UUID warehouseId) {
        List<InventoryMovement> movements =
                orderService.findOrderRequestMovementsByFromWarehouse(warehouseId);
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/{orderId}/confirm-sale")
    public ResponseEntity<String> confirmOrderSale(@PathVariable UUID orderId,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            orderService.confirmOrderSale(orderId,token);
            return ResponseEntity.ok("سفارش با موفقیت به وضعیت SALE تغییر یافت");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{movementId}/process-order-request")
    public ResponseEntity<?> processOrder(@PathVariable UUID movementId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            orderService.processOrder(movementId, token);
            return ResponseEntity.ok().body("انتقال با موفقیت انجام شد !");
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
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        }

    }

    @PostMapping("/cancelOrder/{referenceId}")
    public ResponseEntity<?> processInventory(@PathVariable UUID referenceId) {
        try {
            orderService.cancelOrder(referenceId);
            return ResponseEntity.ok("سفارش با موفقیت لغو شد ");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

//    @PutMapping("{id}")
//    public ResponseEntity<?> updateOrder(@PathVariable UUID id,
//                                         @RequestBody Orders order,
//                                         @RequestParam UUID billingAddressId,
//                                         @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            Orders update = orderService.updateOrder(id, order, billingAddressId, token);
//            return ResponseEntity.ok(update);
//        }catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(Map.of("error", ex.getMessage()));
//
//        } catch (RuntimeException ex) {
//            String msg = ex.getMessage();
//            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", msg));
//            }
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", ex.getMessage()));
//
//        }
//    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(orderService.getOrderById(id));
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

    @GetMapping("/getOrdersByfilter")
    public Page<Orders> getOrdersByfilter(
            @RequestParam(required = false) BigDecimal minTotalAmount,
            @RequestParam(required = false) BigDecimal maxTotalAmount,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) UUID billingAddressId,
            Pageable pageable) {
        return orderService.findOrdersByfilter(
                minTotalAmount,
                maxTotalAmount,
                customerId,
                orderNumber,
                billingAddressId,
                pageable
        ) ;
    }

//    @DeleteMapping("{id}")
//    public ResponseEntity<?> deleteOrder(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            orderService.deleteOrder(id, token);
//            return ResponseEntity.ok().body("سفارش مورد نظر با موفقیت حذف شد");
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(Map.of("error", ex.getMessage()));
//
//        } catch (RuntimeException ex) {
//            String msg = ex.getMessage();
//            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", msg));
//            }
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", ex.getMessage()));
//
//        }
//    }

}
