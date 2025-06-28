package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.BrandService;
import com.example.Sanjaghak.model.Brands;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> addBrand(@RequestBody Brands brand,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Brands savedBrand = brandService.createBrand(brand, token);
            return ResponseEntity.ok(savedBrand);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", msg));
            }
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<?> updateBrand(@PathVariable UUID id, @RequestBody Brands brand,
                                         @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            Brands savedBrand = brandService.updateBrand(id, brand,token);
            return ResponseEntity.ok(savedBrand) ;
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", msg));
            }
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBrand(@PathVariable UUID id) {
        try {
        return ResponseEntity.ok(brandService.getBrandById(id));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", msg));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", msg));
            }

        }
    }

    @GetMapping("/getAllBrands")
    public ResponseEntity<?> getAllBrand() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/getActiveBrands")
    public ResponseEntity<?> getActiveBrand() {
        return ResponseEntity.ok(brandService.getActiveBrands());
    }

    @GetMapping("/getPaginationBrands")
    public Page<Brands> getPaginationBrands(@RequestParam(required = false) String brandName,
                                            Pageable pageable) {
        return brandService.getPaginationBrands(brandName, pageable);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable UUID id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            brandService.deleteBrand(id, token);
            return ResponseEntity.ok(Map.of("message", "برند با موفقیت حذف شد"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", msg));
            }
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

}
