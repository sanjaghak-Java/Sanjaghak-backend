package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.WarehouseRepository;
import com.example.Sanjaghak.model.Suppliers;
import com.example.Sanjaghak.model.Warehouse;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public Warehouse createWarehouse(Warehouse warehouse,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        if (warehouse.getIsCentral()) {
            boolean alreadyRequiredExists = warehouseRepository
                    .existsByIsCentralTrue();
            if (alreadyRequiredExists) {
                throw new RuntimeException("قبلا یک انبار مرکزی ثبت شده است !");
            }
        }

        if(warehouseRepository.existsByPhone(warehouse.getPhone())){
            throw new RuntimeException("انباری با این شماره موبایل ثبت شده است !");
        }

        if(warehouseRepository.existsByPostalCode(warehouse.getPostalCode())){
            throw new RuntimeException("انباری با این کد پستی ثبت شده است !");
        }


        warehouse.setCreatedAt(LocalDateTime.now());
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse (UUID warehouseId ,Warehouse warehouse,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse exist = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انبار مورد نظر پیدا نشد !"));

        if(!exist.getIsCentral().equals(warehouse.getIsCentral())){
            if (warehouse.getIsCentral()) {
                boolean alreadyRequiredExists = warehouseRepository
                        .existsByIsCentralTrue();
                if (alreadyRequiredExists) {
                    throw new RuntimeException("قبلا یک انبار مرکزی ثبت شده است !");
                }
            }
        }

        if(!warehouseRepository.existsByPhone(warehouse.getPhone())){
            if(warehouseRepository.existsByPhone(warehouse.getPhone())){
                throw new RuntimeException("انباری با این شماره موبایل ثبت شده است !");
            }
        }

        if(warehouseRepository.existsByPostalCode(warehouse.getPostalCode())){
            if(warehouseRepository.existsByPostalCode(warehouse.getPostalCode())){
                throw new RuntimeException("انباری با این کد پستی ثبت شده است !");
            }
        }

        exist.setName(warehouse.getName());
        exist.setPhone(warehouse.getPhone());
        exist.setPostalCode(warehouse.getPostalCode());
        exist.setIsCentral(warehouse.getIsCentral());
        exist.setAddress(warehouse.getAddress());
        exist.setCity(warehouse.getCity());
        exist.setCountry(warehouse.getCountry());
        exist.setState(warehouse.getState());

        return warehouseRepository.save(exist);
    }

    public Warehouse getWarehouseById(UUID Warehouse, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return warehouseRepository.findById(Warehouse).orElseThrow(()-> new EntityNotFoundException("انبار مورد نظر پیدا نشد !"));
    }

    public List<Warehouse> getAllWarehouse(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return warehouseRepository.findAll();
    }
}
