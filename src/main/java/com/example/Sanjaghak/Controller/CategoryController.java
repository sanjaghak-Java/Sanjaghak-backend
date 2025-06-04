package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.CategoryService;
import com.example.Sanjaghak.model.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/addCategory")
    public ResponseEntity<?>  addCategory(@RequestBody Categories categories) {
        try {
            String result = categoryService.createCategory(categories);
            if ("دسته‌بندی از قبل وجود دارد !".equals(result)) {
                return ResponseEntity.status(409).body(Map.of("message", result));
            }
            return ResponseEntity.ok(Map.of("message", result));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable UUID id, @RequestBody Categories categories) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categories));
    }

    @GetMapping("{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

}
