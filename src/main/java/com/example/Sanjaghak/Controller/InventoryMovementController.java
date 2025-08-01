package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.InventoryMovementService;
import com.example.Sanjaghak.model.InventoryMovement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/inventoryMovement")
public class InventoryMovementController {
    @Autowired
    private InventoryMovementService inventoryMovementService;

    @PostMapping("/purchaseIn")
    public ResponseEntity<?> purchaseIn(@RequestBody InventoryMovement inventoryMovement,
                                        @RequestParam UUID variantId,
                                        @RequestParam UUID shelvesId,
                                        @RequestParam UUID referenceId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            InventoryMovement save = inventoryMovementService.purchaseIn(inventoryMovement, variantId, shelvesId,referenceId, token);
            return ResponseEntity.ok(save);
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

    @PostMapping("/adjustmentIn")
    public ResponseEntity<?> adjustmentIn(@RequestBody InventoryMovement inventoryMovement,
                                        @RequestParam UUID variantId,
                                        @RequestParam UUID shelvesId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            InventoryMovement save = inventoryMovementService.adjustmentIn(inventoryMovement, variantId, shelvesId, token);
            return ResponseEntity.ok(save);
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

    @PostMapping("/adjustmentOut")
    public ResponseEntity<?> adjustmentOut(@RequestBody InventoryMovement inventoryMovement,
                                          @RequestParam UUID variantId,
                                          @RequestParam UUID shelvesId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            InventoryMovement save = inventoryMovementService.adjustmentOut(inventoryMovement, variantId, shelvesId, token);
            return ResponseEntity.ok(save);
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

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody InventoryMovement inventoryMovement,
                                      @RequestParam UUID variantId,
                                      @RequestParam UUID fromShelvesId,
                                      @RequestParam UUID toShelvesId,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            InventoryMovement save = inventoryMovementService.transfer(inventoryMovement, variantId, fromShelvesId,toShelvesId, token);
            return ResponseEntity.ok(save);
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

    @PostMapping("/requestTransfer")
    public ResponseEntity<?> requestTransfer(@RequestParam int quantity,
                                      @RequestParam UUID variantId,
                                      @RequestParam UUID fromWarehouseId,
                                      @RequestParam UUID toShelvesId,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.requestTransfer(fromWarehouseId, toShelvesId, variantId,quantity, token);
            return ResponseEntity.ok("در خواست انتقال با موفقیت ثبت شد !");
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

    @GetMapping("/getAllTransferRequestByWarehouseId")
    public ResponseEntity<?> getAllTransferRequestByWarehouseId(
                                             @RequestParam UUID fromWarehouseId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<InventoryMovement> movements =  inventoryMovementService.getAllTransferRequestByWarehouseId(fromWarehouseId, token);
            return ResponseEntity.ok(movements);
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


    @GetMapping("/getAllTransferRequestByToWarehouseId")
    public ResponseEntity<?> getAllTransferRequestByToWarehouseId(
            @RequestParam UUID toWarehouseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<InventoryMovement> movements =  inventoryMovementService.getAllTransferRequestByToWarehouseId(toWarehouseId, token);
            return ResponseEntity.ok(movements);
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

    @PostMapping("/shippingTransfer/{movementId}")
    public ResponseEntity<?> shippingTransfer(@PathVariable UUID movementId,
                                              @RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.shippingTransfer(movementId, token);
            return ResponseEntity.ok("در خواست انتقال با موفقیت از انبار خارج شد !");
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

    @GetMapping("/getAllShippingRequestByToWarehouseId")
    public ResponseEntity<?> getAllShippingRequestByToWarehouseId(
            @RequestParam UUID toWarehouseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<InventoryMovement> movements =  inventoryMovementService.getAllShippingRequestByToWarehouseId(toWarehouseId, token);
            return ResponseEntity.ok(movements);
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

    @PostMapping("/transferOut/{movementId}")
    public ResponseEntity<?> transferOut(@PathVariable UUID movementId,
                                              @RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.transferOut(movementId, token);
            return ResponseEntity.ok("در خواست انتقال با موفقیت در انبار در قفسه مناسب جایگذاری شد !");
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

    @PostMapping("/cancelTransfer/{movementId}")
    public ResponseEntity<?> cancelTransfer(@PathVariable UUID movementId,
                                         @RequestHeader("Authorization") String authHeader){
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.cancelTransfer(movementId, token);
            return ResponseEntity.ok("در خواست انتقال با موفقیت لغو شد !");
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

    @GetMapping("{id}")
    public ResponseEntity<?> getInventoryMovementById (@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryMovementService.getInventoryMovementById(id,token));
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

    @GetMapping("/getAllInventoryMovement")
    public ResponseEntity<?> getAllShelves(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryMovementService.getAllInventoryMovement(token));
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

    @PostMapping("/processInventoryStockByReference/{id}")
    public ResponseEntity<?> processInventoryStockByReference(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.processInventoryStockByReference(id,token);
            return ResponseEntity.ok().body("سفارش مرجوعی با موفقیت به موجودی انبار اضافه شد !");
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

    @PostMapping("/saleReturnOut/{id}")
    public ResponseEntity<?> saleReturnOut(@PathVariable UUID id,
                                           @RequestParam int quantity,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            inventoryMovementService.saleReturnOut(id,quantity,token);
            return ResponseEntity.ok().body("موجودی مرجوعی با موفقیت از انبار خارج شد !");
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
