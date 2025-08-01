package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.PurchaseOrderService;
import com.example.Sanjaghak.model.PurchaseOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/purchaseOrders")
public class PurchaseOrdersController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/purchaseOrdersRegistration")
    public ResponseEntity<?> purchaseOrdersRegistration(@RequestBody PurchaseOrders order,
                                               @RequestParam UUID warehouseId,
                                               @RequestParam UUID supplierId,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            PurchaseOrders save = purchaseOrderService.createPurchaseOrder(order, warehouseId,supplierId, token);
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
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatepurchaseOrders(@PathVariable UUID id,
                                                  @RequestBody PurchaseOrders order,
                                                  @RequestParam UUID warehouseId,
                                                  @RequestParam UUID supplierId,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            PurchaseOrders update = purchaseOrderService.updatePurchaseOrders(id, order, warehouseId,supplierId, token);
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
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPurchaseOrdersById (@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(purchaseOrderService.getPurchaseOrdersById(id,token));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @GetMapping("/getAllPurchaseOrders")
    public ResponseEntity<?> getAllPurchaseOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(purchaseOrderService.getAllPurchaseOrders(token));
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelPurchaseOrder(@PathVariable("id") UUID purchaseOrderId,
                                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            purchaseOrderService.cancelPurchaseOrder(purchaseOrderId,token);
            return ResponseEntity.ok("سفارش با موفقیت لغو شد.");
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @PutMapping("/registr/{id}")
    public ResponseEntity<?> registrPurchaseOrder(@PathVariable("id") UUID purchaseOrderId,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            purchaseOrderService.registratPurchaseOrder(purchaseOrderId,token);
            return ResponseEntity.ok("سفارش با موفقیت ثبت شد.");
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

}
