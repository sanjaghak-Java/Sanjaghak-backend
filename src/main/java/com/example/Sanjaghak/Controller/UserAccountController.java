package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.UserAccountsService;
import com.example.Sanjaghak.Service.VerificationService;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/Sanjaghak/UserAccount")
public class UserAccountController {
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserAccountsService userAccountsService;

    public UserAccountController(UserAccountsService userAccountsService, VerificationService verificationService) {
        this.userAccountsService = userAccountsService;
        this.verificationService = verificationService;
    }

    @PostMapping("/requestCode")
    public ResponseEntity<?> requestCode(@RequestBody UserAccounts request) {
        verificationService.sendCode(request.getEmail(),request.getPhoneNumber());
        return ResponseEntity.ok("کد تأیید ارسال شد");
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationToken request) {
        boolean valid = verificationService.verifyCode(
                request.getEmail(),
                request.getPhoneNumber(),
                request.getCode()
        );
        if (valid) {
            return ResponseEntity.ok("کد صحیح است . به سنجاقک خوش اومدین!");

        } else
            return ResponseEntity.badRequest().body("کد اشتباه یا منقضی شده است!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAccounts request) {
        try {
            UserAccounts userAccounts = userAccountsService.register(request);
            return ResponseEntity.ok(userAccounts);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login/requestCode")
    public ResponseEntity<?> requestLoginCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phoneNumber");

        Optional<UserAccounts> user;
        if (email != null && !email.isEmpty()) {
            user = userAccountsService.findByEmail(email);
            verificationService.sendCodeToEmail(email);
        } else if (phone != null && !phone.isEmpty()) {
            user = userAccountsService.findByPhoneNumber(phone);
            verificationService.sendCodeToPhoneNumber(phone);
        } else {
            return ResponseEntity.badRequest().body("ایمیل یا شماره تلفن الزامی است.");
        }

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("کاربری با این مشخصات یافت نشد.");
        }

        return ResponseEntity.ok("کد ورود ارسال شد");
    }

    @PostMapping("/login/verifyCode")
    public ResponseEntity<?> verifyLoginCode(@RequestBody VerificationToken request) {
        boolean valid = false;
        Optional<UserAccounts> userOpt = Optional.empty();

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            valid = verificationService.emailVerifyCode(request.getEmail(), request.getCode());
            userOpt = userAccountsService.findByEmail(request.getEmail());
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            valid = verificationService.smsSVerifyCode(request.getPhoneNumber(), request.getCode());
            userOpt = userAccountsService.findByPhoneNumber(request.getPhoneNumber());
        } else {
            return ResponseEntity.badRequest().body("ایمیل یا شماره تلفن الزامی است");
        }

        if (valid && userOpt.isPresent()) {
            UserAccounts user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userAccountsService.save(user);
            return ResponseEntity.ok("ورود موفقیت‌آمیز بود");
        } else {
            return ResponseEntity.badRequest().body("کد اشتباه یا منقضی شده است");
        }
    }
}
