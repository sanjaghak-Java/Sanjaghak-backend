package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ProductService;
import com.example.Sanjaghak.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody Products product , @RequestParam UUID categoryId , @RequestParam UUID brandId) {
        try{
            Products saveProduct = productService.createProduct(product, categoryId, brandId);
            return ResponseEntity.ok().body(saveProduct);
        }catch (RuntimeException e){
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id , @RequestBody Products product, @RequestParam UUID categoryId , @RequestParam UUID brandId) {
        try{
            Products saveProduct = productService.updateProduct(id, product, categoryId, brandId);
            return ResponseEntity.ok(saveProduct);
        }catch (RuntimeException e){
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }
}
