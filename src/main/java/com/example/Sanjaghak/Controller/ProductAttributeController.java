package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ProductAttributeService;
import com.example.Sanjaghak.model.ProductAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/productAttribute")
public class ProductAttributeController {
    @Autowired
    private ProductAttributeService productAttributeService;

    @PostMapping("/addProductAttribute")
    public ResponseEntity<?> createAttribute(@RequestBody ProductAttribute productAttribute,
                                             @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            ProductAttribute save = productAttributeService.createProductAttribute(productAttribute, token);
            return ResponseEntity.ok(save);

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
    public ResponseEntity<?> updateAttribute(@PathVariable UUID id, @RequestBody ProductAttribute productAttribute,
                                             @RequestHeader("Authorization") String authHeader ){
        try{
            String token = authHeader.replace("Bearer ", "");
            ProductAttribute save = productAttributeService.updateProductAttribute(id, productAttribute, token);
            return ResponseEntity.ok(save);
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

    @GetMapping("{id}")
    public ResponseEntity<?> getProductAttributeById(@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(productAttributeService.getProductAttributeById(id));
        }
         catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @GetMapping("/getAllAttribute")
    public Page<ProductAttribute> getAllProductAttribute(
            @RequestParam(required = false) String attributeName,
            Pageable pageable){
        return productAttributeService.getAllProductAttributes(attributeName,pageable);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProductAttribute(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productAttributeService.deleteProductAttribute(id,token);
            return ResponseEntity.ok().body("ویژگی مورد نظر با موفقیت حذف شد");
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
