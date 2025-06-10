package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.CategoryService;
import com.example.Sanjaghak.model.Categories;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;



    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(@RequestBody Categories category,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Categories result = categoryService.createCategory(category,token);
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

    @PutMapping("{id}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id, @RequestBody Categories categories,
                                                     @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            Categories  savedCategory = categoryService.updateCategory(id, categories, token);
            return ResponseEntity.ok(savedCategory);
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
    public ResponseEntity<Categories> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("getAllCategory")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/getActiveCategory")
    public ResponseEntity<?> getActiveCategory() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @GetMapping("/getPaginationCategory")
    public ResponseEntity<?> getPaginationCategory(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getPaginationCategory(page, size));

    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            categoryService.deleteCategory(id, token);
            return ResponseEntity.ok(Map.of("message", "دسته‌بندی با موفقیت حذف شد"));
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
