package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ProductVariantService;
import com.example.Sanjaghak.model.ProductVariants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/productVariants")
public class ProductVariantsController {

    @Autowired
    private ProductVariantService productVariantService;

    @PostMapping("/addProductVariant")
    public ResponseEntity<?> addProduct(@RequestBody ProductVariants productVariants
            , @RequestParam UUID productId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            ProductVariants saveProductVariant = productVariantService.createProductVariants(productVariants, productId, token);
            return ResponseEntity.ok(saveProductVariant);
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
    public ResponseEntity<?> updateProductVariants (@PathVariable UUID id, @RequestBody ProductVariants productVariants, @RequestParam UUID productId,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            ProductVariants saveProduct = productVariantService.updateProductVariant(id, productVariants, productId, token);
            return ResponseEntity.ok(saveProduct);
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
    public ResponseEntity<?> getProductVariantById (@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(productVariantService.getProductVariantById(id));
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

    @GetMapping("/getAllProductVariant")
    public ResponseEntity<?> getAllProductVariant() {
        return ResponseEntity.ok().body(productVariantService.getAllProductVariants());
    }

    @GetMapping("getProductVariantByProductId")
    public ResponseEntity<?> getProductVariantByProductId(@RequestParam UUID productId) {
        try{
        return ResponseEntity.ok().body(productVariantService.getProductVariantsByProductId(productId));
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

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProductVariant(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productVariantService.deleteProductVariant(id, token);
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
