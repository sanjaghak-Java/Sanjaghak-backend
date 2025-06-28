package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Repository.VerificationTokenRepository;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationService {

    @Autowired
    private final VerificationTokenRepository tokenRepo;

    @Autowired
    private final UserAccountsRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public VerificationService(EmailService emailService, SmsService smsService, VerificationTokenRepository tokenRepo, UserAccountsRepository userRepo) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }

    public String sendCode(String email, String phoneNumber) {

        String code = generateCode();
        VerificationToken token = new VerificationToken();

        token.setEmail(email);
        token.setPhoneNumber(phoneNumber);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);
        token.setCreatedAt(LocalDateTime.now());

        tokenRepo.save(token);
        emailService.sendVerificationEmail(email, code);
        smsService.sendVerificationCode(phoneNumber, code);
        return code;
    }

    public String sendCodeToEmail(String email) {
        String code = generateCode();

        VerificationToken token = new VerificationToken();
        token.setEmail(email);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);

        tokenRepo.save(token);

        emailService.sendVerificationEmail(email, code);

        return code;
    }
    public String sendCodeToPhoneNumber(String phoneNumber) {
        String code = generateCode();

        VerificationToken token = new VerificationToken();

        token.setPhoneNumber(phoneNumber);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);

        tokenRepo.save(token);

        smsService.sendVerificationCode(phoneNumber, code);

        return code;
    }

    public boolean verifyCode(String email, String phoneNumber, String code) {
        return tokenRepo.findByEmailAndPhoneNumberAndCode(email, phoneNumber, code)
                .filter(t -> !t.isVerified() && t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(token -> {
                    token.setVerified(true);
                    tokenRepo.save(token);

                    userRepo.findByEmailAndPhoneNumber(email, phoneNumber).ifPresent(user -> {
                        user.setActive(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepo.save(user);
                    });

                    return true;
                }).orElse(false);
    }

    public boolean isVerified(String email, String phoneNumber) {
        return tokenRepo.findTopByEmailAndPhoneNumberOrderByExpiresAtDesc(email, phoneNumber)
                .filter(t -> t.isVerified() && t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public boolean emailVerifyCode(String email, String code) {
        return tokenRepo.findByEmailAndCode(email, code)
                .filter(t -> !t.isVerified() && t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(token -> {
                    token.setVerified(true);
                    tokenRepo.save(token);

                    // ✅ پیدا کردن کاربر موجود و فعال‌سازی اون
                    Optional<UserAccounts> userOpt = userRepo.findByEmail(email);
                    if (userOpt.isPresent()) {
                        UserAccounts user = userOpt.get();
                        user.setActive(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepo.save(user);
                    }
                    return true;
                }).orElse(false);
    }

    public boolean smsSVerifyCode(String phone, String code) {
        return tokenRepo.findByPhoneNumberAndCode(phone, code)
                .filter(t -> !t.isVerified() && t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(token -> {
                    token.setVerified(true);
                    tokenRepo.save(token);

                    Optional<UserAccounts> userOpt = userRepo.findByPhoneNumber(phone);
                    if (userOpt.isPresent()) {
                        UserAccounts user = userOpt.get();
                        user.setActive(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepo.save(user);
                    }
                    return true;
                }).orElse(false);
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
