package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.FavoritesListService;
import com.example.Sanjaghak.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/favoritesList")
public class FavoritesListController {

    @Autowired
    private FavoritesListService favoritesListService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestParam UUID productId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String result = favoritesListService.toggleFavorite(productId, token);
            return ResponseEntity.ok( result);
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

    @GetMapping("/check")
    public ResponseEntity<?> checkFavoriteWithToken(
            @RequestParam UUID productId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            boolean isInFavorites = favoritesListService.isProductInFavoritesWithToken(productId, token);
            return ResponseEntity.ok(Map.of("exists", isInFavorites));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserFavorites(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<Products> favorites = favoritesListService.getUserFavoriteProducts(token);
            return ResponseEntity.ok(favorites);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }



}
