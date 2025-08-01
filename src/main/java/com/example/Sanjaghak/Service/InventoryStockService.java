package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryStockService {
    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    @Autowired
    private ShelvesRepository shelvesRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SectionsRepository sectionsRepository;

    public InventoryStock createInventoryStock(InventoryStock inventoryStock,UUID variantsId, UUID shelvesId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        ProductVariants variant = productVariantsRepository.findById(variantsId)
                .orElseThrow(() -> new EntityNotFoundException("محصول پیدا نشد!"));

        Shelves shelf = shelvesRepository.findById(shelvesId)
                .orElseThrow(() -> new EntityNotFoundException("قفسه پیدا نشد!"));

        // فقط مسئول قفسه یا ادمین می‌تواند ایجاد کند
        if (!shelf.getUserId().getId().equals(userId) && !role.equalsIgnoreCase("admin")) {
            throw new RuntimeException("شما اجازه ثبت موجودی برای این قفسه را ندارید.");
        }

        if (inventoryStockRepository.existsByShelvesId_ShelvesId(shelvesId)) {
            throw new RuntimeException("یک محصول قبلاً در این قفسه ثبت شده است.");
        }

        UserAccounts creator = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("کاربر پیدا نشد!"));

        InventoryStock stock = new InventoryStock();
        stock.setVariantsId(variant);
        stock.setShelvesId(shelf);
        stock.setQuantityOnHand(0);
        stock.setReservedInventory(0);
        stock.setMinimumLevel(inventoryStock.getMinimumLevel());
        stock.setMaximumLevel(inventoryStock.getMaximumLevel());
        stock.setCreatedBy(creator);
        stock.setUpdatedBy(creator);
        stock.setCreatedAt(LocalDateTime.now());
        stock.setUpdatedAt(LocalDateTime.now());
        stock.setActive(true);

        return inventoryStockRepository.save(stock);
    }

    public InventoryStock updateInventoryStock (UUID inventoryStockId, InventoryStock inventoryStock, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        InventoryStock exist = inventoryStockRepository.findById(inventoryStockId)
                .orElseThrow(() -> new EntityNotFoundException("موجودی پیدا نشد!"));

        if (!exist.getShelvesId().getUserId().getId().equals(userId) && !role.equalsIgnoreCase("admin")) {
            throw new RuntimeException("شما اجازه ثبت موجودی برای این قفسه را ندارید.");
        }

        UserAccounts creator = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("کاربر پیدا نشد!"));

        if(!exist.getActive().equals(inventoryStock.getActive())){
            if(exist.getActive().equals(true)){
                exist.setActive(false);
            }else{
                if(exist.getShelvesId().getActive().equals(false)){
                    throw new RuntimeException("بدون فعال کردن قفسه مربوط ،نمی توان موجودی را فعال کرد!");
                }
                exist.setActive(true);
            }
        }

        exist.setUpdatedBy(creator);
        exist.setUpdatedAt(LocalDateTime.now());
        exist.setMaximumLevel(inventoryStock.getMaximumLevel());
        exist.setMinimumLevel(inventoryStock.getMinimumLevel());
        return inventoryStockRepository.save(exist);
    }

    public InventoryStock getInventoryStockById(UUID purchaseOrdersId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return inventoryStockRepository.findById(purchaseOrdersId).orElseThrow(()-> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));
    }

    public List<InventoryStock> getAllInventoryStock(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return inventoryStockRepository.findAll();
    }

    public List<InventoryStock> getInventoryStocksByWarehouse(UUID warehouseId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انبار پیدا نشد."));

        List<Sections> sections = sectionsRepository.findByWarehouseId(warehouse);

        List<Shelves> shelves = shelvesRepository.findBySectionsIdIn(sections);

        return inventoryStockRepository.findByShelvesIdIn(shelves);
    }

    public List<InventoryStock> getInventoryStocksByVariantIdAndWarehouseId(UUID variantId, UUID warehouseId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انبار پیدا نشد."));

        List<Sections> sections = sectionsRepository.findByWarehouseId(warehouse);
        if (sections.isEmpty()) return Collections.emptyList();

        List<Shelves> shelves = shelvesRepository.findBySectionsIdIn(sections);
        if (shelves.isEmpty()) return Collections.emptyList();

        List<InventoryStock> matchedStocks = inventoryStockRepository
                .findByShelvesIdInAndVariantsId(shelves, new ProductVariants(variantId));

        return matchedStocks;
    }

    public int getTotalStockByVariant(UUID variantId) {
        Integer stock = inventoryStockRepository.getTotalStockByVariantId(variantId);
        return stock != null ? stock : 0;
    }

    public List<InventoryStock> getReturnInventoryStocks(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        return inventoryStockRepository.findByShelvesId_IsReturnTrue();
    }
}
