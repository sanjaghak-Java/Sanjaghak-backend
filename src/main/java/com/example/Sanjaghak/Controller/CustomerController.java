package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.CustomerService;
import com.example.Sanjaghak.model.Customer;
import com.example.Sanjaghak.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/Sanjaghak/Customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PutMapping("{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID id, @RequestParam UUID userId,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Customer updateCustomer = customerService.updateCustomer(id, userId, token);
            return ResponseEntity.ok(updateCustomer);
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
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(customerService.getCustomerById(id));
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

    @GetMapping("/getCustomerByfilter")
    public Page<Customer> getCustomerByfilter(
            @RequestParam(required = false) UUID userId,
            Pageable pageable) {
        return customerService.findCustomerByfilter(
                userId,
                pageable
        ) ;
    }


}
