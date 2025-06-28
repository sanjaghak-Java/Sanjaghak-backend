package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Specification.CategorySpecifications;
import com.example.Sanjaghak.Specification.UserSpecification;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
            throw new RuntimeException("کاربر قبلاً ثبت شده است.");
        }

        if (userAccountsRepository.existsByEmail(userAccounts.getEmail())) {
            throw new RuntimeException("ایمیل قبلاً استفاده شده است.");
        }

        if (userAccountsRepository.existsByPhoneNumber(userAccounts.getPhoneNumber())) {
            throw new RuntimeException("شماره تلفن قبلاً استفاده شده است.");
        }


        userAccounts.setActive(true);
        userAccounts.setCreatedAt(LocalDateTime.now());
        userAccounts.setUpdatedAt(LocalDateTime.now());

        return userAccountsRepository.save(userAccounts);
    }
    public UserAccounts adminRegister(UserAccounts userAccounts , String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") ) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (userAccountsRepository.existsByEmail(userAccounts.getEmail()) &&
                userAccountsRepository.existsByPhoneNumber(userAccounts.getPhoneNumber())) {
            throw new RuntimeException("کاربر قبلاً ثبت شده است.");
        }

        if (userAccountsRepository.existsByEmail(userAccounts.getEmail())) {
            throw new RuntimeException("ایمیل قبلاً استفاده شده است.");
        }

        if (userAccountsRepository.existsByPhoneNumber(userAccounts.getPhoneNumber())) {
            throw new RuntimeException("شماره تلفن قبلاً استفاده شده است.");
        }

        if (userAccounts.getRole() == null) {
            throw new RuntimeException("نقش کاربر خالی است!");
        }

        if (!isValidRole(userAccounts.getRole().name())) {
            throw new IllegalArgumentException("نقش کاربر معتبر نیست!");
        }


        userAccounts.setActive(true);
        userAccounts.setCreatedAt(LocalDateTime.now());
        userAccounts.setUpdatedAt(LocalDateTime.now());

        return userAccountsRepository.save(userAccounts);
    }

    public boolean isValidRole(String roleInput) {
        try {
            boolean isValidRole = false;
        for (User_role r : User_role.values()) {
            if (r.name().equalsIgnoreCase(roleInput)) {
                isValidRole = true;
                break;
            }
        }
        return isValidRole;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<UserAccounts> findByEmail(String email) {
        if(!userAccountsRepository.existsByEmail(email)) {
            throw new RuntimeException("کاربری با این ایمیل وجود ندارد!");
        }
        UserAccounts userAccounts= userAccountsRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));
        if(userAccounts.getActive()){
            return Optional.of(userAccounts);
        }else {
            throw new RuntimeException("حساب شما فعلا از دسترس خارج شده است از شکیبایی شما سپاس گزارم.");
        }
    }

    public Optional<UserAccounts> findByPhoneNumber(String phoneNumber) {
        return userAccountsRepository.findByPhoneNumber(phoneNumber);
    }

    public UserAccounts save(UserAccounts user) {
        return userAccountsRepository.save(user);
    }

    public UserAccounts updateUser(UUID id, UserAccounts request, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);


        UserAccounts user = userAccountsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        if(!(request.getEmail().equals(user.getEmail()))) {
            if(userAccountsRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("ادرس ایمیل قبلا استفاده شده است!");
            }
        }

        if(!(request.getPhoneNumber().equals(user.getPhoneNumber()))) {
            if(userAccountsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("شماره موبایل قبلا استفاده شده است!");
            }
        }

        if (role.equalsIgnoreCase("admin") ) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setActive(request.getActive());
            user.setUpdatedAt(LocalDateTime.now());
            user.setRole(User_role.valueOf(request.getRole().name()));
            user.setPhoneNumber(request.getPhoneNumber());
            user.setEmail(request.getEmail());
            return userAccountsRepository.save(user);
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        return userAccountsRepository.save(user);
    }

    public Page<UserAccounts> getPaginationUser(String name,Boolean active,User_role role, Pageable pageable,String token) {
        String naghsh = JwtUtil.extractUserRole(token);

        if (!naghsh.equalsIgnoreCase("admin")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return userAccountsRepository.findAll(UserSpecification.filterUser(name,active,role),pageable);
    }

    public UserAccounts getUserById(UUID id) {
        return userAccountsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("کاربر مورد نظر پیدا نشد !"));
    }

    public String getUserRole(String token) {
        String naghsh = JwtUtil.extractUserRole(token);
        return naghsh;
    }

}
