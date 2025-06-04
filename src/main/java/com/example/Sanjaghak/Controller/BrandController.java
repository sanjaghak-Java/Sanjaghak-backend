package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.BrandService;
import com.example.Sanjaghak.model.Brands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/addBrand")
    public ResponseEntity<?> addBrand(@RequestBody Brands brand) {
        try {
            Brands savedBrand = brandService.createBrand(brand);
            return ResponseEntity.ok(savedBrand);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<?> updateBrand(@PathVariable UUID id, @RequestBody Brands brand) {
        try{
            Brands savedBrand = brandService.updateBrand(id, brand);
            return ResponseEntity.ok(savedBrand) ;
        }catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBrand(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllBrand() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

}
