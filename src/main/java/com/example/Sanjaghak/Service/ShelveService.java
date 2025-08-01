package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.User_role;
import com.example.Sanjaghak.Repository.SectionsRepository;
import com.example.Sanjaghak.Repository.ShelvesRepository;
import com.example.Sanjaghak.Repository.UserAccountsRepository;
import com.example.Sanjaghak.model.Sections;
import com.example.Sanjaghak.model.Shelves;
import com.example.Sanjaghak.model.UserAccounts;
import com.example.Sanjaghak.model.Warehouse;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

@Service
public class ShelveService {
    @Autowired
    private ShelvesRepository shelvesRepository;

    @Autowired
    private SectionsRepository sectionsRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public Shelves createShelves(Shelves shelves , UUID userId , UUID sectionsId , String token) {

        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Sections sections = sectionsRepository.findById(sectionsId)
                .orElseThrow(() -> new EntityNotFoundException("بخش مورد نظر پیدا نشد !"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("انباردار مورد نظر پیدا نشد !"));

        if(!user.getRole().equals(User_role.staff)){
            throw new RuntimeException("فقط می توانید یک انبار دار را مسئول یک قفسه کنید .");
        }

        if (Boolean.TRUE.equals(shelves.getReturn())) {
            Warehouse warehouse = sections.getWarehouseId();  // گرفتن انبار مربوط به سکشن
            if (warehouse == null || !Boolean.TRUE.equals(warehouse.getIsCentral())) {
                throw new RuntimeException("قفسه بازگشتی فقط باید در انبار مرکزی ایجاد شود.");
            }
        }

        List<Shelves> existingShelves = shelvesRepository.findBySectionsId(sections);

        String sectionName = sections.getName();
        String prefix = sectionName.length() >= 3
                ? sectionName.substring(0, 3).toUpperCase()
                : sectionName.toUpperCase(); // ✅ ۳ حرف اول


        int nextCodeNumber = 1;

        if (!existingShelves.isEmpty()) {
            OptionalInt maxCode = existingShelves.stream()
                    .map(Shelves::getShelvesCode)
                    .map(code -> code.split("-")[1]) // "001"
                    .mapToInt(Integer::parseInt)
                    .max();

            nextCodeNumber = maxCode.orElse(0) + 1;
        }

        String shelvesCode = String.format("%s-%03d", prefix, nextCodeNumber);
        shelves.setShelvesCode(shelvesCode);
        shelves.setSectionsId(sections);
        shelves.setUserId(user);
        shelves.setCreatedAt(LocalDateTime.now());

        return shelvesRepository.save(shelves);
    }

    public Shelves updateShelves(UUID shelvesId, Shelves shelves, UUID sectionsId, UUID userId, String token) {

        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("انباردار مورد نظر پیدا نشد !"));

        if (!user.getRole().equals(User_role.staff)) {
            throw new RuntimeException("فقط می‌توان یک انباردار را به قفسه اختصاص داد.");
        }

        Sections newSection = sectionsRepository.findById(sectionsId)
                .orElseThrow(() -> new EntityNotFoundException("بخش مورد نظر پیدا نشد !"));

        Shelves existingShelf = shelvesRepository.findById(shelvesId)
                .orElseThrow(() -> new EntityNotFoundException("قفسه مورد نظر پیدا نشد !"));

        Sections oldSection = existingShelf.getSectionsId();
        String oldShelvesCode = existingShelf.getShelvesCode();

        existingShelf.setReturn(shelves.getReturn());

        if (Boolean.TRUE.equals(shelves.getReturn())) {
            Warehouse warehouse = newSection.getWarehouseId();
            if (warehouse == null || !Boolean.TRUE.equals(warehouse.getIsCentral())) {
                throw new RuntimeException("قفسه مرجوعی فقط باید در انبار مرکزی باشد.");
            }
        }

        if (!oldSection.getSectionsId().equals(sectionsId)) {

            List<Shelves> newSectionShelves = shelvesRepository.findBySectionsId(newSection);

            String sectionName = newSection.getName();
            String prefix = sectionName.length() >= 3
                    ? sectionName.substring(0, 3).toUpperCase()
                    : sectionName.toUpperCase(); // ✅ ۳ حرف اول

            int nextCodeNumber = 1;
            if (!newSectionShelves.isEmpty()) {
                OptionalInt maxCode = newSectionShelves.stream()
                        .map(Shelves::getShelvesCode)
                        .map(code -> code.split("-")[1])
                        .mapToInt(Integer::parseInt)
                        .max();
                nextCodeNumber = maxCode.orElse(0) + 1;
            }

            String newCode = String.format("%s-%03d", prefix, nextCodeNumber);

            existingShelf.setSectionsId(newSection);
            existingShelf.setShelvesCode(newCode);

            List<Shelves> oldSectionShelves = shelvesRepository.findBySectionsId(oldSection);

            int oldNumber = Integer.parseInt(oldShelvesCode.split("-")[1]);
            String oldPrefix = oldShelvesCode.split("-")[0];

            for (Shelves s : oldSectionShelves) {
                if (!s.getShelvesId().equals(existingShelf.getShelvesId())) {
                    String[] parts = s.getShelvesCode().split("-");
                    String sPrefix = parts[0];
                    int sNumber = Integer.parseInt(parts[1]);

                    if (sPrefix.equals(oldPrefix) && sNumber > oldNumber) {
                        String updatedCode = String.format("%s-%03d", sPrefix, sNumber - 1);
                        s.setShelvesCode(updatedCode);
                        shelvesRepository.save(s);
                    }
                }
            }
        }

        existingShelf.setUserId(user);
        return shelvesRepository.save(existingShelf);
    }


    public Shelves getShelvesById(UUID shelvesId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return shelvesRepository.findById(shelvesId).orElseThrow(()-> new EntityNotFoundException("قفسه مورد نظر پیدا نشد !"));
    }

    public List<Shelves> getAllShelves(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return shelvesRepository.findAll();
    }

    public List<Shelves> getShelvesBySectionId(UUID sectionId ,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return shelvesRepository.findBySectionsId_SectionsId(sectionId);
    }




}
