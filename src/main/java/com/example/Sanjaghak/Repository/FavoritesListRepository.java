package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.FavoritesList;
import com.example.Sanjaghak.model.Products;
import com.example.Sanjaghak.model.UserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritesListRepository extends JpaRepository<FavoritesList, UUID>, JpaSpecificationExecutor<FavoritesList> {

    Optional<FavoritesList> findByUserIdAndProductId(UserAccounts user, Products product);
    List<FavoritesList> findAllByUserId(UserAccounts user);


}
