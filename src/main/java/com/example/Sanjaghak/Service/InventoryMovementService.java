package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.MovementType;
import com.example.Sanjaghak.Enum.Statuses;
import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    @Autowired
    private ShelvesRepository shelvesRepository;

    @Autowired
    private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private PurchaseOrdersRepository purchaseOrdersRepository;

    @Autowired
    private SectionsRepository sectionsRepository;


    @Autowired
    private WarehouseRepository warehouseRepository;

    public InventoryMovement purchaseIn (InventoryMovement inventoryMovement, UUID variantId,UUID shelvesId,UUID referenceId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ProductVariants productVariants = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        Shelves shelves = shelvesRepository.findById(shelvesId)
                .orElseThrow(() -> new RuntimeException("قفسه پیدا نشد"));

        PurchaseOrderItems purchaseOrderItems = purchaseOrderItemsRepository.findById(referenceId)
                .orElseThrow(() -> new RuntimeException("ایتم خرید مورد نظر پیدا نشد"));

        PurchaseOrders purchaseOrders = purchaseOrdersRepository.findById(purchaseOrderItems.getPurchaseOrdersId().getPurchaseOrdersId())
                .orElseThrow(() -> new RuntimeException("سفارش مورد نظر  پیدا نشد"));

        if(!purchaseOrders.getStatus().equals(Statuses.Shipping)){
            throw new RuntimeException("شما نمی توانید از این سفارش برای افزایش موجودی استفاده کنید");
        }

        if(!inventoryStockRepository.existsByVariantsIdAndShelvesId(productVariants,shelves)){
            throw new RuntimeException("نمی توان یک محصول را به یک قفسه نامربوط وصل کرد!");
        }

        InventoryStock inventoryStock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(shelves,productVariants)
                .orElseThrow(() -> new RuntimeException("موجودی مورد نظر پیدا نشد"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        int curentQuantityOnHand = inventoryStock.getQuantityOnHand();

        int newQuantityOnHand = curentQuantityOnHand + inventoryMovement.getQuantity();
        inventoryStock.setQuantityOnHand(newQuantityOnHand);
        inventoryStock.setUpdatedBy(user);
        inventoryStock.setUpdatedAt(LocalDateTime.now());


        int curentRecivedQuantity = purchaseOrderItems.getRecivedQuantity();
        if(curentRecivedQuantity + inventoryMovement.getQuantity() > purchaseOrderItems.getQuantityOrdered()){
            throw new RuntimeException("مقدار دریافتی از تعداد سفارش بیشتر است !");
        }
        purchaseOrderItems.setRecivedQuantity(curentRecivedQuantity+inventoryMovement.getQuantity());



        InventoryMovement movement = new InventoryMovement();
        movement.setVariantsId(productVariants);
        movement.setToShelvesId(shelves);
        movement.setRefrenceId(purchaseOrderItems.getPurchaseOrderItemsId());
        movement.setQuantity(inventoryMovement.getQuantity());
        movement.setMovementType(MovementType.PURCHASE_IN);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setCreatedBy(user);

        purchaseOrderItemsRepository.save(purchaseOrderItems);
        inventoryStockRepository.save(inventoryStock);
        return inventoryMovementRepository.save(movement);
    }

    public InventoryMovement adjustmentIn(InventoryMovement inventoryMovement, UUID variantId,UUID shelvesId,String token){
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ProductVariants productVariants = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        Shelves shelves = shelvesRepository.findById(shelvesId)
                .orElseThrow(() -> new RuntimeException("قفسه پیدا نشد"));

        if(!inventoryStockRepository.existsByVariantsIdAndShelvesId(productVariants,shelves)){
            throw new RuntimeException("نمی توان یک محصول را به یک قفسه نامربوط وصل کرد!");
        }

        InventoryStock inventoryStock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(shelves,productVariants)
                .orElseThrow(() -> new RuntimeException("موجودی مورد نظر پیدا نشد"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        int curentQuantityOnHand = inventoryStock.getQuantityOnHand();
        int newQuantityOnHand = curentQuantityOnHand + inventoryMovement.getQuantity();
        inventoryStock.setQuantityOnHand(newQuantityOnHand);
        inventoryStock.setUpdatedBy(user);
        inventoryStock.setUpdatedAt(LocalDateTime.now());

        inventoryMovement.setVariantsId(productVariants);
        inventoryMovement.setToShelvesId(shelves);
        inventoryMovement.setRefrenceId(user.getId());
        inventoryMovement.setMovementType(MovementType.ADJUSTMENT_IN);
        inventoryMovement.setCreatedAt(LocalDateTime.now());
        inventoryMovement.setCreatedBy(user);
        return inventoryMovementRepository.save(inventoryMovement);
    }

    public InventoryMovement adjustmentOut(InventoryMovement inventoryMovement, UUID variantId,UUID shelvesId,String token){
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ProductVariants productVariants = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        Shelves shelves = shelvesRepository.findById(shelvesId)
                .orElseThrow(() -> new RuntimeException("قفسه پیدا نشد"));

        if(!inventoryStockRepository.existsByVariantsIdAndShelvesId(productVariants,shelves)){
            throw new RuntimeException("نمی توان یک محصول را به یک قفسه نامربوط وصل کرد!");
        }

        InventoryStock inventoryStock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(shelves,productVariants)
                .orElseThrow(() -> new RuntimeException("موجودی مورد نظر پیدا نشد"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        int curentQuantityOnHand = inventoryStock.getQuantityOnHand();
        int newQuantityOnHand = curentQuantityOnHand - inventoryMovement.getQuantity();
        if(newQuantityOnHand<0){
            throw new RuntimeException("نمی توان بیشتر از موجودی محصول از موجودی کم کرد!");
        }
        inventoryStock.setQuantityOnHand(newQuantityOnHand);
        inventoryStock.setUpdatedBy(user);
        inventoryStock.setUpdatedAt(LocalDateTime.now());

        inventoryMovement.setVariantsId(productVariants);
        inventoryMovement.setToShelvesId(shelves);
        inventoryMovement.setRefrenceId(user.getId());
        inventoryMovement.setMovementType(MovementType.ADJUSTMENT_OUT);
        inventoryMovement.setCreatedAt(LocalDateTime.now());
        inventoryMovement.setCreatedBy(user);
        return inventoryMovementRepository.save(inventoryMovement);
    }

    public InventoryMovement transfer(InventoryMovement inventoryMovement, UUID variantId,UUID fromShelvesId, UUID toShelvesId,String token){
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ProductVariants productVariants = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

        Shelves fromShelves = shelvesRepository.findById(fromShelvesId)
                .orElseThrow(() -> new RuntimeException("قفسه مبدا پیدا نشد"));

        Shelves toShelves = shelvesRepository.findById(toShelvesId)
                .orElseThrow(() -> new RuntimeException("قفسه مقصد پیدا نشد"));


        Sections fromSection = sectionsRepository.findById(fromShelves.getSectionsId().getSectionsId())
                .orElseThrow(() -> new RuntimeException("بخش مبدا پیدا نشد"));

        Sections toSection = sectionsRepository.findById(toShelves.getSectionsId().getSectionsId())
                .orElseThrow(() -> new RuntimeException("بخش مقصد پیدا نشد"));

        if(!fromSection.getWarehouseId().getWarehouseId().equals(toSection.getWarehouseId().getWarehouseId())){
            throw new RuntimeException("نمی توان یک محصول را بین قفسه های انبارهای مختلف انتقال داد!");
        }

        if(!inventoryStockRepository.existsByVariantsIdAndShelvesId(productVariants,fromShelves)){
            throw new RuntimeException("نمی توان یک محصول را به یک قفسه نامربوط وصل کرد!");
        }

        if(!inventoryStockRepository.existsByVariantsIdAndShelvesId(productVariants,toShelves)){
            throw new RuntimeException("نمی توان یک محصول را به یک قفسه نامربوط وصل کرد!");
        }

        InventoryStock fromInventoryStock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(fromShelves,productVariants)
                .orElseThrow(() -> new RuntimeException("موجودی مبدا مورد نظر پیدا نشد"));

        InventoryStock toInventoryStock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(toShelves,productVariants)
                .orElseThrow(() -> new RuntimeException("موجودی مقصد مورد نظر پیدا نشد"));

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        int fromCurentQuantityOnHand = fromInventoryStock.getQuantityOnHand();
        int toCurentQuantityOnHand = toInventoryStock.getQuantityOnHand();
        int fromNewQuantityOnHand = fromCurentQuantityOnHand - inventoryMovement.getQuantity();
        if(fromNewQuantityOnHand<0){
            throw new RuntimeException("نمی توان بیشتر از موجودی محصول از موجودی کم کرد!");
        }
        int toNewQuantityOnHand = toCurentQuantityOnHand + inventoryMovement.getQuantity();
        fromInventoryStock.setQuantityOnHand(fromNewQuantityOnHand);
        fromInventoryStock.setUpdatedBy(user);
        fromInventoryStock.setUpdatedAt(LocalDateTime.now());

        toInventoryStock.setQuantityOnHand(toNewQuantityOnHand);
        toInventoryStock.setUpdatedBy(user);
        toInventoryStock.setUpdatedAt(LocalDateTime.now());


        inventoryMovement.setVariantsId(productVariants);
        inventoryMovement.setToShelvesId(toShelves);
        inventoryMovement.setFromShelvesId(fromShelves);
        inventoryMovement.setRefrenceId(user.getId());
        inventoryMovement.setMovementType(MovementType.TRANSFERING_IN);
        inventoryMovement.setCreatedAt(LocalDateTime.now());
        inventoryMovement.setCreatedBy(user);
        inventoryStockRepository.save(toInventoryStock);
        inventoryStockRepository.save(fromInventoryStock);
        return inventoryMovementRepository.save(inventoryMovement);
    }

    public void requestTransfer(UUID fromWarehouseId,UUID toShelvesId,UUID variantId,int quantity,String token ){
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse fromWarehouse = warehouseRepository.findById(fromWarehouseId).orElseThrow(() -> new RuntimeException("انبار مبدا پیدا نشد"));

        Shelves toShelves = shelvesRepository.findById(toShelvesId).orElseThrow(() -> new RuntimeException("قفسه مقصد پیدا نشد"));

        ProductVariants variants = productVariantsRepository.findById(variantId).orElseThrow(() -> new RuntimeException("محصول یافت نشد"));

        UserAccounts user = userAccountsRepository.findById(userId).orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        Sections sections = sectionsRepository.findById(toShelves.getSectionsId().getSectionsId()).orElseThrow(() -> new RuntimeException("بخش مقصد یافت نشد !"));

        // دریافت موجودی‌های فعال این ورینت در انبار
        List<InventoryStock> sourceStocks = inventoryStockRepository
                .findByVariantsId_VariantIdAndShelvesId_SectionsId_WarehouseIdAndIsActive(
                        variantId, fromWarehouse, true
                );

        if (sourceStocks.isEmpty()) {
            throw new RuntimeException("موجودی فعالی برای این کالا در انبار مبدا یافت نشد !");
        }

        // مرتب‌سازی بر اساس تاریخ آپدیت (قدیمی‌تر اول)
        sourceStocks.sort(Comparator.comparing(InventoryStock::getUpdatedAt));

        // بررسی کافی بودن موجودی
        int totalAvailable = sourceStocks.stream()
                .mapToInt(InventoryStock::getQuantityOnHand)
                .sum();

        if (totalAvailable < quantity) {
            throw new IllegalStateException("موجودی کافی از این کالا در انبار مبدا وجود ندارد!");
        }

        int remainingQty = quantity;

        // کم کردن از قفسه‌ها به ترتیب قدیمی‌تر
        for (InventoryStock stock : sourceStocks) {
            if (remainingQty <= 0) break;

            int availableQty = stock.getQuantityOnHand();
            if (availableQty <= 0) continue;

            int qtyToReserve = Math.min(availableQty, remainingQty);

            stock.setQuantityOnHand(stock.getQuantityOnHand() - qtyToReserve);
            stock.setReservedInventory(stock.getReservedInventory() + qtyToReserve);
            stock.setUpdatedAt(LocalDateTime.now());
            inventoryStockRepository.save(stock);

            // ایجاد رکورد برای هر قفسه
            InventoryMovement movement = new InventoryMovement();
            movement.setVariantsId(variants);
            movement.setFromWarehouseId(fromWarehouse);
            movement.setToWarehouseId(sections.getWarehouseId());
            movement.setFromShelvesId(stock.getShelvesId());
            movement.setToShelvesId(toShelves);
            movement.setQuantity(qtyToReserve);
            movement.setMovementType(MovementType.REQUEST_TRANSFER);
            movement.setCreatedAt(LocalDateTime.now());
            movement.setRefrenceId(user.getId());
            movement.setCreatedBy(user);

            inventoryMovementRepository.save(movement);

            remainingQty -= qtyToReserve;
        }
    }


    public List<InventoryMovement> getAllTransferRequestByWarehouseId(UUID warehouseId,String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse fromWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("انبار مبدا پیدا نشد"));
        List<InventoryMovement> movements = inventoryMovementRepository.findByFromWarehouseIdAndMovementType(fromWarehouse, MovementType.REQUEST_TRANSFER);
        return movements;
    }

    public List<InventoryMovement> getAllTransferRequestByToWarehouseId(UUID warehouseId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse ToWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("انبار مقصد پیدا نشد"));
        List<InventoryMovement> movements = inventoryMovementRepository.findByToWarehouseIdAndMovementType(ToWarehouse, MovementType.REQUEST_TRANSFER);

        return movements;
    }

    public void shippingTransfer(UUID movementId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        InventoryMovement movement = inventoryMovementRepository.findById(movementId).orElseThrow(() -> new RuntimeException("در خواست انتقال یافت نشد !"));

        if(!movement.getMovementType().equals(MovementType.REQUEST_TRANSFER)) {
            throw new RuntimeException("شما نمی توانید این انتقال را انجام دهید !");
        }

        InventoryStock stock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(movement.getFromShelvesId(),movement.getVariantsId()).orElseThrow(() -> new RuntimeException("موجودی یافت نشد !"));

        if(stock.getReservedInventory() < movement.getQuantity()) {
            throw new RuntimeException("موجودی کافی برای این انتقال وجود ندارد !");
        }

        stock.setReservedInventory(stock.getReservedInventory() - movement.getQuantity());
        inventoryStockRepository.save(stock);

        movement.setMovementType(MovementType.SHIPPING_TRANSFER);
        inventoryMovementRepository.save(movement);
    }

    public List<InventoryMovement> getAllShippingRequestByToWarehouseId(UUID warehouseId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Warehouse ToWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("انبار مقصد پیدا نشد"));
        List<InventoryMovement> movements = inventoryMovementRepository.findByToWarehouseIdAndMovementType(ToWarehouse, MovementType.SHIPPING_TRANSFER);

        return movements;
    }

    public void transferOut(UUID movementId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        InventoryMovement movement = inventoryMovementRepository.findById(movementId).orElseThrow(() -> new RuntimeException("در خواست انتقال یافت نشد !"));

        if(!movement.getMovementType().equals(MovementType.SHIPPING_TRANSFER)) {
            throw new RuntimeException("شما نمی توانید این انتقال را انجام دهید !");
        }

        InventoryStock stock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(movement.getToShelvesId(),movement.getVariantsId()).orElseThrow(() -> new RuntimeException("موجودی یافت نشد !"));

        stock.setQuantityOnHand(stock.getQuantityOnHand() + movement.getQuantity());
        inventoryStockRepository.save(stock);

        movement.setMovementType(MovementType.TRANSFERING_OUT);
        inventoryMovementRepository.save(movement);
    }

    public void cancelTransfer(UUID movementId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        InventoryMovement movement = inventoryMovementRepository.findById(movementId).orElseThrow(() -> new RuntimeException("در خواست انتقال یافت نشد !"));


        if(!movement.getMovementType().equals(MovementType.REQUEST_TRANSFER)) {
            throw new RuntimeException("شما نمی توانید این انتقال را لغو کنید !");

        }

        InventoryStock stock = inventoryStockRepository.findInventoryStockByShelvesIdAndVariantsId(movement.getFromShelvesId(),movement.getVariantsId()).orElseThrow(() -> new RuntimeException("موجودی یافت نشد !"));

        if(stock.getReservedInventory() < movement.getQuantity()) {
            throw new RuntimeException("موجودی کافی برای لغو کردن انتقال وجود ندارد !");
        }

        stock.setReservedInventory(stock.getReservedInventory() - movement.getQuantity());
        stock.setQuantityOnHand(stock.getQuantityOnHand() + movement.getQuantity());
        inventoryStockRepository.save(stock);
        movement.setMovementType(MovementType.CANCEL_TRANSFER);
        inventoryMovementRepository.save(movement);
    }

    public InventoryMovement getInventoryMovementById(UUID inventoryMovementId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return inventoryMovementRepository.findById(inventoryMovementId).orElseThrow(()-> new EntityNotFoundException("انتقال مورد نظر پیدا نشد !"));
    }

    public List<InventoryMovement> getAllInventoryMovement(String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        return inventoryMovementRepository.findAll();
    }

    @Transactional
    public void processInventoryStockByReference(UUID referenceId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);
        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        // 1. دریافت لیست InventoryMovement با referenceId و SALE_RETURN_REQUEST
        List<InventoryMovement> movements = inventoryMovementRepository
                .findByRefrenceIdAndMovementType(referenceId, MovementType.SALE_RETURN_REQUEST);

        for (InventoryMovement movement : movements) {
            Warehouse warehouse = movement.getToWarehouseId();
            ProductVariants variant = movement.getVariantsId();

            // 2. پیدا کردن قفسه‌های برگشتی فعال در انبار
            List<Shelves> shelvesList = shelvesRepository.findBySectionsId_WarehouseIdAndIsReturnTrueAndIsActiveTrue(warehouse);

            for (Shelves shelf : shelvesList) {
                // 3. بررسی InventoryStock برای این قفسه و این وریانت
                Optional<InventoryStock> stockOpt = inventoryStockRepository.findByVariantsIdAndShelvesIdAndIsActiveTrue(variant, shelf);

                if (stockOpt.isPresent()) {
                    InventoryStock stock = stockOpt.get();
                    stock.setReservedInventory(stock.getReservedInventory() + movement.getQuantity());
                    stock.setUpdatedBy(user);
                    stock.setUpdatedAt(LocalDateTime.now());
                    inventoryStockRepository.save(stock);
                    movement.setToShelvesId(stock.getShelvesId());
                } else {
                    InventoryStock newStock = new InventoryStock();
                    newStock.setVariantsId(variant);
                    newStock.setShelvesId(shelf);
                    newStock.setQuantityOnHand(0);
                    newStock.setReservedInventory(movement.getQuantity());
                    newStock.setMinimumLevel(0);
                    newStock.setMaximumLevel(0);
                    newStock.setCreatedBy(user);
                    newStock.setActive(true);
                    newStock.setCreatedAt(LocalDateTime.now());

                    inventoryStockRepository.save(newStock);
                    movement.setToShelvesId(newStock.getShelvesId());
                }
            }
            movement.setMovementType(MovementType.SALE_RETURN_IN);
        }
    }

    public void saleReturnOut(UUID inventoryStockId,int quantity, String token) {

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        UserAccounts user = userAccountsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر پیدا نشد"));

        InventoryStock inventoryStock = inventoryStockRepository.findById(inventoryStockId)
                .orElseThrow(() -> new RuntimeException("موجودی یافت نشد "));

        if(inventoryStock.getShelvesId().getReturn().equals(false)){
            throw new RuntimeException("موجودی انتخاب شده مربوط به موجودی مرجوعی کالا ها نیست !");
        }

        if(inventoryStock.getReservedInventory()<quantity){
            throw new RuntimeException("مقدار خروجی بیشتر از موجودی است !");
        }
        inventoryStock.setReservedInventory(inventoryStock.getReservedInventory() - quantity);
        inventoryStock.setUpdatedBy(user);
        inventoryStock.setUpdatedAt(LocalDateTime.now());
        inventoryStockRepository.save(inventoryStock);

        InventoryMovement inventoryMovement = new InventoryMovement();
        inventoryMovement.setMovementType(MovementType.SALE_RETURN_OUT);
        inventoryMovement.setFromShelvesId(inventoryStock.getShelvesId());
        inventoryMovement.setFromWarehouseId(inventoryStock.getShelvesId().getSectionsId().getWarehouseId());
        inventoryMovement.setQuantity(quantity);
        inventoryMovement.setCreatedAt(LocalDateTime.now());
        inventoryMovement.setCreatedBy(user);
        inventoryMovement.setRefrenceId(inventoryStockId);
        inventoryMovement.setVariantsId(inventoryStock.getVariantsId());

        inventoryMovementRepository.save(inventoryMovement);
    }







}

