package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Service.CustomerService;
import com.example.Sanjaghak.Service.UserAccountsService;
import com.example.Sanjaghak.Service.VerificationService;
import com.example.Sanjaghak.model.Categories;
import com.example.Sanjaghak.model.Customer;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.model.VerificationToken;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/UserAccount")
public class UserAccountController {
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserAccountsService userAccountsService;

    @Autowired
    private CustomerService customerService;

    public UserAccountController(UserAccountsService userAccountsService, VerificationService verificationService) {
        this.userAccountsService = userAccountsService;
        this.verificationService = verificationService;
    }


    @PostMapping("/requestCode")
    public ResponseEntity<?> requestCode(@RequestBody UserAccounts request) {
        verificationService.sendCode(request.getEmail(), request.getPhoneNumber());
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
            return ResponseEntity.ok("کد صحیح است. حالا می‌تونی ثبت‌نام کنی.");
        } else {
            return ResponseEntity.badRequest().body("کد اشتباه یا منقضی شده است.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAccounts request) {
        boolean isVerified = verificationService.isVerified(request.getEmail(), request.getPhoneNumber());

        if (!isVerified) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "کد تأیید هنوز وارد نشده یا نامعتبر است"));
        }
        try {
            request.setRole(User_role.customer);
            UserAccounts userAccounts = userAccountsService.register(request);
            customerService.saveCustomer(userAccounts.getId());
            String jwtToken = JwtUtil.generateToken(userAccounts);
            return ResponseEntity.ok(Map.of(
                    "message", "ورود موفقیت‌آمیز بود",
                    "token", jwtToken,
                    "id" , userAccounts.getId()
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }



    @PostMapping("/login/requestCode")
    public ResponseEntity<?> requestLoginCode(@RequestBody Map<String, String> request) {
        try {
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
        }catch (IllegalArgumentException ex) {
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

            String jwtToken = JwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of(
                    "message", "ورود موفقیت‌آمیز بود",
                    "token", jwtToken,
                    "id" , user.getId()
            ));
        } else {
            return ResponseEntity.badRequest().body("کد اشتباه یا منقضی شده است");
        }
    }

    @PostMapping("/adminRegistration")
    public ResponseEntity<?> adminRegistration(@RequestBody UserAccounts request,
    @RequestHeader("Authorization") String authHeader) {
        if (request.getRole() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "نقش نمی‌تواند خالی باشد"));
        }
        String roleStr = request.getRole().name();
        boolean isValidRole = false;
        for (User_role r : User_role.values()) {
            if (r.name().equalsIgnoreCase(roleStr)) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "نقش وارد شده معتبر نیست"));
        }
        try {
            String token = authHeader.replace("Bearer ", "");
            UserAccounts userAccounts = userAccountsService.adminRegister(request , token);
            String jwtToken = JwtUtil.generateToken(userAccounts);
            return ResponseEntity.ok(Map.of(
                    "message", "ورود موفقیت‌آمیز بود",
                    "token", jwtToken
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/updateUsers/{id}")
    public ResponseEntity<?> updateUsers(@PathVariable UUID id, @RequestBody UserAccounts request, @RequestHeader("Authorization") String authHeader) {
        try{
            if (request.getEmail() == null ) {
                throw new RuntimeException("ادرس ایمیل نباید خالی باشد!");
            }
            if (request.getPhoneNumber() == null ) {
                throw new RuntimeException("شماره موبایل نباید خالی باشد!");
            }
            String token = authHeader.replace("Bearer ", "");
            UserAccounts update = userAccountsService.updateUser(id, request, token);
            return ResponseEntity.ok(update);
        }catch (IllegalArgumentException ex) {
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

    @GetMapping("/getPaginationUser")
    public ResponseEntity<?> getPaginationUser(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) Boolean active,
                                              @RequestParam(required = false) User_role role,
                                                  Pageable pageable,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(userAccountsService.getPaginationUser(name, active, role, pageable, token));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", msg));
            }
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(userAccountsService.getUserById(id));
        }catch (IllegalArgumentException ex) {
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
    @GetMapping("/getUserRole")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(userAccountsService.getUserRole(token));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", msg));
            }
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }

    }

}
