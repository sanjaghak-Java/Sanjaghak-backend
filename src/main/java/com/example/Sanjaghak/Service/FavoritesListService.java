package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.FavoritesListRepository;
import com.example.Sanjaghak.Repository.ProductRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.FavoritesList;
import com.example.Sanjaghak.model.Products;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoritesListService {
    @Autowired
    private FavoritesListRepository favoritesListRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private ProductRepository productRepository;

    public String toggleFavorite(UUID productId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("محصول مورد نظر پیدا نشد !"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Optional<FavoritesList> existingFavorite = favoritesListRepository.findByUserIdAndProductId(user, product);

        if (existingFavorite.isPresent()) {
            favoritesListRepository.delete(existingFavorite.get());
            return "محصول مورد نظر شما با موفقیت از لیست علاقه مندی های شما حذف شد!";
        } else {
            FavoritesList newFavorite = new FavoritesList();
            newFavorite.setUserId(user);
            newFavorite.setProductId(product);
            newFavorite.setCreatedAt(LocalDateTime.now());
            favoritesListRepository.save(newFavorite);
            return "محصول مورد نظر شما با موفقیت به لیست علاقه مندی های شما افزوده شد!";
        }
    }

    public boolean isProductInFavoritesWithToken(UUID productId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        return favoritesListRepository.findByUserIdAndProductId(user, product).isPresent();
    }

    public List<Products> getUserFavoriteProducts(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        List<FavoritesList> favorites = favoritesListRepository.findAllByUserId(user);

        return favorites.stream()
                .map(FavoritesList::getProductId)
                .collect(Collectors.toList());
    }


}
