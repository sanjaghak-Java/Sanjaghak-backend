package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.CategoryImageService;
import com.example.Sanjaghak.model.CategoryImage;
import com.example.Sanjaghak.model.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/categoryImages")
public class CategoryImageController {
    @Autowired
    private CategoryImageService categoryImageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam(value = "altText", required = false) String altText ,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            CategoryImage save =  categoryImageService.handleImageUpload(file, categoryId, altText,token);
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
            @RequestParam UUID categoryId,
            @RequestParam("file") MultipartFile newFile,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            CategoryImage result = categoryImageService.update(imageId, categoryId, newFile, altText, token);
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
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "خطا در آپلود فایل جدید: " + e.getMessage()));
        }
    }


    @GetMapping("/getAllImage")
    public ResponseEntity<List<CategoryImage>> getAllImages() {
        return ResponseEntity.ok(categoryImageService.findAll());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<List<CategoryImage>> getImagesByCategoryId(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryImageService.findByCategoryId(categoryId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID imageId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            categoryImageService.delete(imageId,token);
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
