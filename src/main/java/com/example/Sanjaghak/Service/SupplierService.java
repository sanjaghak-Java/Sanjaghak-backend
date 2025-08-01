package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.SuppliersRepository;
import com.example.Sanjaghak.model.Suppliers;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    @Autowired
    private SuppliersRepository suppliersRepository;

    public Suppliers createSupplier(Suppliers suppliers, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        suppliers.setCreatedAt(LocalDateTime.now());
        suppliers.setUpdatedAt(LocalDateTime.now());

        return suppliersRepository.save(suppliers);
    }

    public Suppliers updateSuppliers (UUID supplierId , Suppliers suppliers, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Suppliers supplier = suppliersRepository.findById(supplierId)
                .orElseThrow(() -> new EntityNotFoundException("تامیین کننده مورد نظر پیدا نشد !"));


        supplier.setSupplierName(suppliers.getSupplierName());
        supplier.setSupplierEmail(suppliers.getSupplierEmail());
        supplier.setSupplierPhone(suppliers.getSupplierPhone());
        supplier.setSupplierAddress(suppliers.getSupplierAddress());
        supplier.setCity(suppliers.getCity());
        supplier.setState(suppliers.getState());
        supplier.setCountry(suppliers.getCountry());
        supplier.setPostalCode(suppliers.getPostalCode());
        supplier.setUpdatedAt(LocalDateTime.now());

        return suppliersRepository.save(supplier);
    }

    public Suppliers getSuppliersById(UUID suppliers) {
        return suppliersRepository.findById(suppliers).orElseThrow(()-> new EntityNotFoundException("تامین کننده مورد نظر پیدا نشد !"));
    }

    public List<Suppliers> getAllSuppliers() {
        return suppliersRepository.findAll();
    }

    public void deleteSuppliers (UUID suppliers,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        if (!suppliersRepository.existsById(suppliers)) {
            throw new IllegalArgumentException("محصول مورد نظر یافت نشد.");
        }
        Suppliers delete = suppliersRepository.findById(suppliers).orElseThrow((() -> new EntityNotFoundException("تامین کننده مورد نظر پیدا نشد !")));
        suppliersRepository.delete(delete);
    }
}
