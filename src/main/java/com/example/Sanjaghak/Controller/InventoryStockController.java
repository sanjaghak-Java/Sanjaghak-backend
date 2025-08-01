package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.InventoryStockService;
import com.example.Sanjaghak.model.InventoryStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/inventoryStock")
public class InventoryStockController {

    @Autowired
    private InventoryStockService inventoryStockService;

    @PostMapping("/create")
    public ResponseEntity<?> createInventoryStock(
            @RequestBody InventoryStock inventoryStock,
            @RequestParam UUID variantsId,
            @RequestParam UUID shelvesId,
            @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            InventoryStock created = inventoryStockService.createInventoryStock(inventoryStock,variantsId, shelvesId, token);
            return ResponseEntity.ok(created);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما اجازه ثبت موجودی برای این قفسه را ندارید.".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @PutMapping("/{variantsId}")
    public ResponseEntity<?> updateInventoryStock(
            @PathVariable UUID variantsId,
            @RequestBody InventoryStock inventoryStock,
            @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            InventoryStock update = inventoryStockService.updateInventoryStock(variantsId,inventoryStock, token);
            return ResponseEntity.ok(update);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما اجازه ثبت موجودی برای این قفسه را ندارید.".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryStockById (@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryStockService.getInventoryStockById(id,token));
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

    @GetMapping("/getAllInventoryStock")
    public ResponseEntity<?> getAllInventoryStock(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryStockService.getAllInventoryStock(token));
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

    @GetMapping("/getInventoryStocksByWarehouse/{id}")
    public ResponseEntity<?> getInventoryStocksByWarehouse (@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryStockService.getInventoryStocksByWarehouse(id,token));
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

    @GetMapping("/getInventoryStocksByVariantIdAndWarehouseId")
    public ResponseEntity<?> getInventoryStocksByVariantId (@RequestParam UUID variantId,@RequestParam UUID warehouseId ,@RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryStockService.getInventoryStocksByVariantIdAndWarehouseId(variantId,warehouseId,token));
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

    @GetMapping("/variant/{variantId}/stock")
    public ResponseEntity<Integer> getTotalStockByVariant(@PathVariable UUID variantId) {
        int totalStock = inventoryStockService.getTotalStockByVariant(variantId);
        return ResponseEntity.ok(totalStock);
    }

    @GetMapping("/getReturnInventoryStocks")
    public ResponseEntity<?> getReturnInventoryStocks (@RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok().body(inventoryStockService.getReturnInventoryStocks(token));
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
