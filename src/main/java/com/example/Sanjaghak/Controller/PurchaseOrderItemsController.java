package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.PurchaseOrderItemService;
import com.example.Sanjaghak.model.PurchaseOrderItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/purchaseOrderItems")
public class PurchaseOrderItemsController {
    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @PostMapping("/purchaseOrdersItemRegistration")
    public ResponseEntity<?> purchaseOrdersItemRegistration(@RequestBody PurchaseOrderItems order,
                                                        @RequestParam UUID purchaseOrderId,
                                                        @RequestParam UUID variantsId,
                                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            PurchaseOrderItems save = purchaseOrderItemService.save(order, purchaseOrderId,variantsId, token);
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
    public ResponseEntity<?> updatePurchaseOrderItems(@PathVariable UUID id,
                                                      @RequestBody PurchaseOrderItems order,
                                                      @RequestParam UUID purchaseOrderId,
                                                      @RequestParam UUID variantsId,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            PurchaseOrderItems update = purchaseOrderItemService.updatePurchaseOrderItems(id, order, purchaseOrderId,variantsId, token);
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
    public ResponseEntity<?> getPurchaseOrdersItemById (@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(purchaseOrderItemService.getPurchaseOrdersItemById(id,token));
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

    @GetMapping("/getAllPurchaseOrdersItem")
    public ResponseEntity<?> getAllPurchaseOrdersItem(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(purchaseOrderItemService.getAllPurchaseOrdersItem(token));
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

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<?> getItemsByOrderId(@PathVariable UUID orderId,@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<PurchaseOrderItems> items = purchaseOrderItemService.getPurchaseOrdersItemsByorderId(orderId,token);
            if (items.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(items);
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


    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePurchaseOrderItems(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            purchaseOrderItemService.deletePurchaseOrderItems(id, token);
            return ResponseEntity.ok().body("سفارش مورد نظر با موفقیت حذف شد");
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
}
