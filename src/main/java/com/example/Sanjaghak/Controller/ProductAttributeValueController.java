package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ProductAttributeValueService;
import com.example.Sanjaghak.model.ProductAttribute;
import com.example.Sanjaghak.model.ProductAttributeValue;
import com.example.Sanjaghak.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/productAttributeValue")
public class ProductAttributeValueController {
    @Autowired
    private ProductAttributeValueService productAttributeValueService;

    @PostMapping("addValue")
    public ResponseEntity<?> createProductAttributeValue(@RequestParam UUID productId, @RequestParam UUID attributeId,
                                                         @RequestBody ProductAttributeValue productAttributeValue,
                                                         @RequestHeader("Authorization") String authHeader) {
        try {

            String token = authHeader.replace("Bearer ", "");
            ProductAttributeValue save = productAttributeValueService.createProductAttributeValue(productId, attributeId,
                    productAttributeValue, token);
            return ResponseEntity.ok(save);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        }
        catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            }
            else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProductAttributeValue(@PathVariable UUID id,@RequestParam UUID productId,
                                                         @RequestParam UUID attributeId,
                                                         @RequestBody ProductAttributeValue productAttributeValue,
                                                         @RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ", "");
            ProductAttributeValue save = productAttributeValueService.updateProductAttributeValue(id,productId,attributeId,productAttributeValue,token);
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
    public ResponseEntity<?> getProductAttributeValueById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(productAttributeValueService.getProductAttributeValueById(id));
    }

    @GetMapping("/getValueByProductId/{productId}")
    public ResponseEntity<?> getProductAttributeValueByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok().body(productAttributeValueService.getProductAttributeValueByProductId(productId));
    }

    @GetMapping("/getAllAttributeValue")
    public ResponseEntity<?> getAllProductAttributeValue() {
        return ResponseEntity.ok().body(productAttributeValueService.getAllProductAttributeValues());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProductAttribute(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productAttributeValueService.deleteProductAttributeValue(id,token);
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
