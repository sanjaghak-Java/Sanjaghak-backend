package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ProductService;
import com.example.Sanjaghak.model.Products;
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
@RequestMapping("/api/Sanjaghak/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping(value = "/addProduct", consumes = "application/json")
    public ResponseEntity<?> addProduct(@RequestBody Products product
            , @RequestParam UUID categoryId, @RequestParam UUID brandId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Products saveProduct = productService.createProduct(product, categoryId, brandId, token);
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

    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id, @RequestBody Products product, @RequestParam UUID categoryId,
                                           @RequestParam UUID brandId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Products saveProduct = productService.updateProduct(id, product, categoryId, brandId, token);
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
    public ResponseEntity<?> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    @GetMapping("/getAllProduct")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/getActiveProducts")
    public ResponseEntity<?> getActiveProducts() {
        return ResponseEntity.ok().body(productService.getActiveProducts());
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProductAttributeRequiremenr(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productService.deleteProduct(id, token);
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

    @PostMapping("/validateAttributes")
    public ResponseEntity<?> validateAttributesAndActivate(@RequestParam UUID productId,
                                                           @RequestParam UUID categoryId,
                                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Object result = productService.validateAndActivateProduct(productId, categoryId, token);
            return ResponseEntity.ok(result);
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public Page<Products> getProductsByfilter(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            Pageable pageable) {
        return productService.findProductsByfilter(
                productName,
                minPrice,
                maxPrice,
                active,
                categoryId,
                brandId,
                pageable
        );
    }
}


