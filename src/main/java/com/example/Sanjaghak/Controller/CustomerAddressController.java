package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.CustomerAddressService;
import com.example.Sanjaghak.model.Customer;
import com.example.Sanjaghak.model.CustomerAddress;
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
@RequestMapping("/api/Sanjaghak/customerAddress")
public class CustomerAddressController {
    @Autowired
    private CustomerAddressService customerAddressService;

    @PostMapping("/addCustomerAddress")
    public ResponseEntity<?> addCustomerAddress(@RequestBody CustomerAddress customerAddress,@RequestParam (required = false) UUID customerId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            CustomerAddress save = customerAddressService.create(customerAddress, token,customerId);
            return ResponseEntity.ok(save);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
    public ResponseEntity<?> updatedAddress(@PathVariable UUID id, @RequestBody CustomerAddress updateCustomerAddress
                                         , @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            CustomerAddress update = customerAddressService.updateAddress(id,updateCustomerAddress, token);
            return ResponseEntity.ok(update);
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
    public ResponseEntity<?> getAddressById(@PathVariable UUID id) {
        try{
            return ResponseEntity.ok().body(customerAddressService.getAddressById(id));
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

    @GetMapping("/getAddressByfilter")
    public Page<CustomerAddress> getAddressByfilter(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) UUID customerId,
            Pageable pageable) {
        return customerAddressService.findAddressByfilter(
                city,
                state,
                country,
                postalCode,
                customerId,
                pageable
        ) ;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            customerAddressService.deleteAddress(id);
            return ResponseEntity.ok().body("آدرس مورد نظر با موفقیت حذف شد");
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
}
