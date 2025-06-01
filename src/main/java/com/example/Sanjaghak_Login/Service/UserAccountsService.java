package com.example.Sanjaghak_Login.Service;

import com.example.Sanjaghak_Login.Repository.UserAccountsRepository;
import com.example.Sanjaghak_Login.model.UserAccounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAccountsService {
    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public UserAccountsService(UserAccountsRepository userAccountsRepository) {
        this.userAccountsRepository = userAccountsRepository;
    }

    public UserAccounts register(UserAccounts userAccounts) {
        if (userAccountsRepository.existsByEmail(userAccounts.getEmail()) &&
                userAccountsRepository.existsByPhoneNumber(userAccounts.getPhoneNumber())) {
            throw new RuntimeException("کاربر قبلاً ثبت شده است");
        }

        if (userAccountsRepository.existsByEmail(userAccounts.getEmail())) {
            throw new RuntimeException("ایمیل قبلاً ثبت شده است");
        }

        if (userAccountsRepository.existsByPhoneNumber(userAccounts.getPhoneNumber())) {
            throw new RuntimeException("شماره تلفن قبلاً ثبت شده است");
        }

        userAccounts.setActive(true);
        userAccounts.setCreatedAt(LocalDateTime.now());
        userAccounts.setUpdatedAt(LocalDateTime.now());

        return userAccountsRepository.save(userAccounts);
    }

    public Optional<UserAccounts> findByEmail(String email) {
        return userAccountsRepository.findByEmail(email);
    }

    public Optional<UserAccounts> findByPhoneNumber(String phoneNumber) {
        return userAccountsRepository.findByPhoneNumber(phoneNumber);
    }

    public UserAccounts save(UserAccounts user) {
        return userAccountsRepository.save(user);
    }


}
