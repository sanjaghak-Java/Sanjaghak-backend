package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.CustomerAddressRepository;
import com.example.Sanjaghak.Repository.CustomerRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.Specification.CustomerAddressSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerAddressService {

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public CustomerAddress create(CustomerAddress customerAddress,String token,UUID customerId) {

        String role = JwtUtil.extractUserRole(token);
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));

        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("manager")) {
            if((customerId == null)) {
                throw new IllegalArgumentException("کاربر مورد نظر وارد نشده است !");
            }

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("مشتری پیدا نشد"));

            customerAddress.setCustomerId(customer);
        }

        if(customerRepository.existsByUserId_Id(userId)){
            Customer customer = customerRepository.findByUserId_Id(userId)
                    .orElseThrow(() -> new RuntimeException("مشتری پیدا نشد"));
            customerAddress.setCustomerId(customer);
        }

        if((customerAddress.getCity() == null)) {
            throw new IllegalArgumentException("شهر مورد نظر وارد نشده است !");
        }
        if((customerAddress.getCountry() == null)) {
            throw new IllegalArgumentException("کشور مورد نظر وارد نشده است !");
        }
        if((customerAddress.getPostalCode() == null)) {
            throw new IllegalArgumentException("کد پستی مورد نظر وارد نشده است !");
        }

        customerAddress.setCreatedAt(LocalDateTime.now());
        customerAddress.setUpdatedAt(LocalDateTime.now());
        return customerAddressRepository.save(customerAddress);
    }

    public CustomerAddress updateAddress(UUID addressId, CustomerAddress updatedCustomerAddress,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (!customerAddressRepository.existsById(addressId)) {
            throw new IllegalArgumentException("آدرس مشتری مورد نظر یافت نشد.");
        }

        if (role.equalsIgnoreCase("customer")) {
            if(updatedCustomerAddress.getCustomerId() != null) {
                if(customerRepository.existsByUserId_Id(userId)){
                    Customer customer = customerRepository.findByUserId_Id(userId)
                            .orElseThrow(() -> new RuntimeException("مشتری پیدا نشد"));
                    if(!customer.getCustomerId().equals(updatedCustomerAddress.getCustomerId())) {
                        throw new RuntimeException("تو مجوز لازم برای انجام این عملیات را ندارید");
                    }
                }
            }
        }

        CustomerAddress existing = customerAddressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("مشتری مورد نظر پیدا نشد !"));

        if((updatedCustomerAddress.getCity() == null)) {
            throw new IllegalArgumentException("شهر مورد نظر وارد نشده است !");
        }
        if((updatedCustomerAddress.getCountry() == null)) {
            throw new IllegalArgumentException("کشور مورد نظر وارد نشده است !");
        }
        if((updatedCustomerAddress.getPostalCode() == null)) {
            throw new IllegalArgumentException("کد پستی مورد نظر وارد نشده است !");
        }


        existing.setAddressLine1(updatedCustomerAddress.getAddressLine1());
        existing.setAddressLine2(updatedCustomerAddress.getAddressLine2());
        existing.setCity(updatedCustomerAddress.getCity());
        existing.setState(updatedCustomerAddress.getState());
        existing.setCountry(updatedCustomerAddress.getCountry());
        existing.setPostalCode(updatedCustomerAddress.getPostalCode());
        existing.setPhone(updatedCustomerAddress.getPhone());
        existing.setUpdatedAt(LocalDateTime.now());

        return customerAddressRepository.save(existing);
    }

    public CustomerAddress getAddressById(UUID addressId) {
        return customerAddressRepository.findById(addressId).orElseThrow(()-> new EntityNotFoundException("آدرس مورد نظر پیدا نشد !"));
    }

    public Page<CustomerAddress> findAddressByfilter(
            String city,
            String state,
            String country,
            String postalCode,
            UUID customerId,
            Pageable pageable) {

        return customerAddressRepository.findAll(
                CustomerAddressSpecifications.filterAddresses(
                        city,
                        state,
                        country,
                        postalCode,
                        customerId
                ),
                pageable
        );
    }

    public void deleteAddress(UUID addressId) {
        CustomerAddress customerAddress = customerAddressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("آدرس مورد نظر پیدا نشد !"));

        customerAddressRepository.delete(customerAddress);
    }
}
