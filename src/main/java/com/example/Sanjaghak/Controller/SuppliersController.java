package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.SupplierService;
import com.example.Sanjaghak.model.ProductVariants;
import com.example.Sanjaghak.model.Suppliers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/suppliers")
public class SuppliersController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/addSuppliers")
    public ResponseEntity<?> createSuppliers (@RequestBody Suppliers supplier, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Suppliers saveSuppliers = supplierService.createSupplier(supplier, token);
            return ResponseEntity.ok(saveSuppliers);
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

    @PutMapping("{id}")
    public ResponseEntity<?> updateSuppliers (@PathVariable UUID id, @RequestBody Suppliers suppliers,
                                                    @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Suppliers update = supplierService.updateSuppliers(id, suppliers, token);
            return ResponseEntity.ok(update);
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
    public ResponseEntity<?> getSuppliersById (@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(supplierService.getSuppliersById(id));
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

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<?> getAllSuppliers() {
        return ResponseEntity.ok().body(supplierService.getAllSuppliers());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSuppliers(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            supplierService.deleteSuppliers(id, token);
            return ResponseEntity.ok().body("محصول مورد نظر با موفقیت حذف شد");
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
