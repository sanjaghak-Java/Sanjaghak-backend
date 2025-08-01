package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.InventoryStockRepository;
import com.example.Sanjaghak.Repository.SectionsRepository;
import com.example.Sanjaghak.Repository.ShelvesRepository;
import com.example.Sanjaghak.Repository.WarehouseRepository;
import com.example.Sanjaghak.model.InventoryStock;
import com.example.Sanjaghak.model.Sections;
import com.example.Sanjaghak.model.Shelves;
import com.example.Sanjaghak.model.Warehouse;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SectionService {

    @Autowired
    private SectionsRepository sectionsRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ShelvesRepository shelvesRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    public Sections createSections(Sections sections, UUID warehouseId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انبار مورد نظر پیدا نشد !"));

        String name = sections.getName();

        if (!name.matches("^[a-zA-Z0-9]{3}$")) {
            throw new RuntimeException("نام بخش باید فقط شامل حروف انگلیسی یا اعداد باشد و دقیقاً ۳ کاراکتر باشد.");
        }

        boolean sectionExists = sectionsRepository.existsByNameAndWarehouseId(name, warehouse);
        if (sectionExists) {
            throw new RuntimeException("بخشی با این نام قبلاً در این انبار ثبت شده است.");
        }

        sections.setWarehouseId(warehouse);
        sections.setCreatedAt(LocalDateTime.now());
        return sectionsRepository.save(sections);
    }


    public Sections updateSections(UUID sectionsId, Sections sections, UUID warehouseId, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انبار مورد نظر پیدا نشد !"));

        Sections exist = sectionsRepository.findById(sectionsId)
                .orElseThrow(() -> new EntityNotFoundException("بخش مورد نظر پیدا نشد !"));

        boolean nameChanged = !exist.getName().equals(sections.getName());

        if (nameChanged) {
            String name = sections.getName();

            if (!name.matches("^[a-zA-Z0-9]{3}$")) {
                throw new RuntimeException("نام بخش باید فقط شامل حروف انگلیسی یا اعداد باشد و دقیقاً ۳ کاراکتر باشد.");
            }

            boolean sectionExists = sectionsRepository.existsByNameAndWarehouseId(name, warehouse);
            if (sectionExists) {
                throw new RuntimeException("بخشی با این نام قبلاً در این انبار ثبت شده است.");
            }
        }

        if (!warehouseId.equals(exist.getWarehouseId().getWarehouseId())) {
            boolean sectionExists = sectionsRepository.existsByNameAndWarehouseId(sections.getName(), warehouse);
            if (sectionExists) {
                throw new RuntimeException("بخشی با این نام قبلاً در این انبار ثبت شده است.");
            }
        }

        exist.setName(sections.getName());
        exist.setWarehouseId(warehouse);

        if (nameChanged) {
            String newPrefix = sections.getName().toUpperCase(); // کل نام چون ۳ حرفی هست

            List<Shelves> shelvesList = shelvesRepository.findBySectionsId(exist);

            shelvesList.sort(Comparator.comparing(Shelves::getShelvesCode));

            int counter = 1;
            for (Shelves shelf : shelvesList) {
                String newCode = String.format("%s-%03d", newPrefix, counter++);
                shelf.setShelvesCode(newCode);
                shelvesRepository.save(shelf);
            }
        }

        if(!exist.getActive().equals(sections.getActive())){
            if(exist.getActive().equals(true)){
                exist.setActive(false);
                List<Shelves> shelvesList = shelvesRepository.findBySectionsId(exist);
                List<InventoryStock> inventoryStockList = inventoryStockRepository.findByShelvesIdIn(shelvesList);

                for(Shelves shelf :shelvesList ){
                    shelf.setActive(false);
                }
                shelvesRepository.saveAll(shelvesList);

                for(InventoryStock inventoryStock: inventoryStockList){
                    inventoryStock.setActive(false);
                }
                inventoryStockRepository.saveAll(inventoryStockList);
            }else{
                if(exist.getWarehouseId().getIsActive().equals(false)){
                    throw new RuntimeException("نمی توان بدون فعال کردن انبار بخش مربوط به ان انبار را فعال کرد !");
                }
                exist.setActive(true);
            }
        }

        return sectionsRepository.save(exist);
    }



    public Sections getSectionsById(UUID sections, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return sectionsRepository.findById(sections).orElseThrow(()-> new EntityNotFoundException("بخش مورد نظر پیدا نشد !"));
    }

    public List<Sections> getAllSections(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return sectionsRepository.findAll();
    }

    public List<Sections> getSectionsByWarehouseId(UUID warehouseId ,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return sectionsRepository.findByWarehouseId_WarehouseId(warehouseId);
    }


}
