package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.model.UserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountsRepository extends JpaRepository<UserAccounts, UUID> {
    // جستجو با ایمیل
    Optional<UserAccounts> findByEmail(String email);

    Optional<UserAccounts> findById(UUID id);

    // جستجو با شماره تلفن
    Optional<UserAccounts> findByPhoneNumber(String phoneNumber);

    Optional<UserAccounts> findByEmailAndPhoneNumber(String email, String phoneNumber);

    // بررسی وجود کاربر با ایمیل خاص
    boolean existsByEmail(String email);

    // بررسی وجود کاربر با شماره تلفن خاص
    boolean existsByPhoneNumber(String phoneNumber);

    // جستجو کاربران با نقش خاص (مثلاً "admin" یا "customer")
    List<UserAccounts> findByRole(User_role role);

    // پیدا کردن کاربران فعال
    List<UserAccounts> findByIsActiveTrue();

    // پیدا کردن کاربران غیرفعال
    List<UserAccounts> findByIsActiveFalse();





}
