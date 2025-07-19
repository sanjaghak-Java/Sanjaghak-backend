package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Service.ProductImageService;
import com.example.Sanjaghak.model.ProductImage;
import com.example.Sanjaghak.model.Products;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/productImages")
public class ProductImageController {

    @Autowired
    private  ProductImageService productImageService;

    private final Path uploadPath = Paths.get("D:/springbootproject/Sanjaghak/Sanjaghak-backend/media");


    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") UUID productId,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "required", required = false) boolean required,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            ProductImage save = productImageService.handleImageUpload(file, productId, altText, required, token);
            return ResponseEntity.ok(save);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("خطا در آپلود: " + e.getMessage());
        }
    }



    @PutMapping("/{imageId}")
    public ResponseEntity<?> updateImage(
            @PathVariable UUID imageId,
            @RequestParam UUID productId,
            @RequestBody ProductImage updatedImage,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            ProductImage result = productImageService.update(imageId,productId, updatedImage,token);
            return ResponseEntity.ok(result);
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

    @GetMapping("/getAllImage")
    public ResponseEntity<List<ProductImage>> getAllImages() {
        return ResponseEntity.ok(productImageService.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImage>> getImagesByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(productImageService.findByProductId(productId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID imageId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productImageService.delete(imageId,token);
            return ResponseEntity.ok("تصویر حذف شد.");
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
