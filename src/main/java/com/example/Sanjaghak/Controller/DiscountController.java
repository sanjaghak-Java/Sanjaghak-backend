package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.DiscountService;
import com.example.Sanjaghak.model.Discount;
import com.example.Sanjaghak.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/discount")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @PostMapping(value = "/addDiscount")
    public ResponseEntity<?> addDiscount(@RequestBody Discount discount
            , @RequestParam UUID variantId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Discount save = discountService.createDiscount(discount, variantId, token);
            return ResponseEntity.ok(save);
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

    @PutMapping("{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable UUID id, @RequestBody Discount discount, @RequestParam UUID variantId,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Discount saveDiscount = discountService.updateDiscount(id, discount, variantId, token);
            return ResponseEntity.ok(saveDiscount);
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

    @GetMapping("{id}")
    public ResponseEntity<?> getDiscountById (@PathVariable UUID id) {
        try{

            return ResponseEntity.ok().body(discountService.getDiscountById(id));
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

    @GetMapping("/getAllDiscount")
    public ResponseEntity<?> getAllDiscount() {
        try {
            return ResponseEntity.ok().body(discountService.getAllDiscount());
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

    @GetMapping("/active/{variantId}")
    public ResponseEntity<?> getActiveDiscount(@PathVariable UUID variantId) {
        Discount discount = discountService.getCurrentActiveDiscount(variantId);
        if (discount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("تخفیف فعالی برای این محصول در حال حاضر وجود ندارد.");
        }
        return ResponseEntity.ok(discount);
    }

    @GetMapping("/max-discount/{productId}")
    public ResponseEntity<?> getMaxDiscount(@PathVariable UUID productId) {
        Discount discount = discountService.getMaxActiveDiscountByProduct(productId);
        if (discount == null) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }
        return ResponseEntity.ok(discount);
    }



}
