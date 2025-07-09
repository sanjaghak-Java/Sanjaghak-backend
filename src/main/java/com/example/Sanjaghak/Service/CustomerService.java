package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CustomerRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Specification.CustomerSpecification;
import com.example.Sanjaghak.Specification.ProductSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public Customer saveCustomer(UUID id){


        UserAccounts user = userAccountsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Customer customer = new Customer();
        customer.setUserId(user);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(UUID customertId, UUID userId, String token) {

        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!customerRepository.existsById(customertId)) {
            throw new IllegalArgumentException("مشتری مورد نظر یافت نشد.");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        if(!(user.getRole().toValue().equals("customer"))){
            throw new RuntimeException("ایدی که برای مشتری انتخاب شده مناسب مشری نیست !" + user.getRole());
        }

        Customer existing = customerRepository.findById(customertId)
                .orElseThrow(() -> new EntityNotFoundException("مشتری مورد نظر پیدا نشد !"));

        existing.setUserId(user);
        existing.setUpdatedAt(LocalDateTime.now());

        return customerRepository.save(existing);
    }

    public Customer getCustomerById(UUID customerId) {
        return customerRepository.findById(customerId).orElseThrow(()-> new EntityNotFoundException("مشتری مورد نظر پیدا نشد !"));
    }

    public Page<Customer> findCustomerByfilter(
            UUID userId,
            Pageable pageable) {
        return customerRepository.findAll(
                CustomerSpecification.filterCustomer(userId),
                pageable
        );
    }

}
