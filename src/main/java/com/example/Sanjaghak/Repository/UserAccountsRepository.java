package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.UserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountsRepository extends JpaRepository<UserAccounts, UUID> , JpaSpecificationExecutor<UserAccounts> {
    Optional<UserAccounts> findByEmail(String email);

    Optional<UserAccounts> findById(UUID id);

    Optional<UserAccounts> findByPhoneNumber(String phoneNumber);

    Optional<UserAccounts> findByEmailAndPhoneNumber(String email, String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<UserAccounts> findByRole(User_role role);

    List<UserAccounts> findByIsActiveTrue();

    List<UserAccounts> findByIsActiveFalse();
}
