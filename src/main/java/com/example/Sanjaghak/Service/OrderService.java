package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.MovementType;
import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.Specification.OrderSpecifications;
import com.example.Sanjaghak.model.*;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    public Orders createOrder(UUID id) {

        Customer customer = customerRepository.findByUserId_Id(id)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));

        Orders order = new Orders();
        order.setCustomerId(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderNumber(generateUniqueOrderNumber());
        order.setShippingCost(new BigDecimal("1000000.00"));
        order.setOrderStatus(OrderStatus.pending);

        return ordersRepository.save(order);
    }

    public Orders reorder(UUID originalOrderId, String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));

        Customer customer = customerRepository.findByUserId_Id(userId)
                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));

        Orders originalOrder = ordersRepository.findById(originalOrderId)
                .orElseThrow(() -> new RuntimeException("سفارش اولیه پیدا نشد"));

        List<OrderItem> originalItems = orderItemRepository.findByOrderId(originalOrder);
        orderItemRepository.deleteAll(originalItems);

        CustomerAddress customerAddress = customerAddressRepository.findByCustomerId(customer)
                .orElseThrow(() -> new RuntimeException("آدرس پیدا نشد"));

        Orders newOrder = new Orders();
        newOrder.setCustomerId(originalOrder.getCustomerId());
        newOrder.setBillingAddressId(customerAddress);
        newOrder.setOrderStatus(OrderStatus.processing);
        newOrder.setOrderNumber(generateUniqueOrderNumber());
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        ordersRepository.save(newOrder);

        BigDecimal totalSubTotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (OrderItem originalItem : originalItems) {
            UUID variantId = originalItem.getVariantId().getVariantId();
            int quantity = originalItem.getQuantity();

            Warehouse centralWarehouse = warehouseRepository.findByIsCentralAndIsActive(true, true)
                    .orElseThrow(() -> new RuntimeException("انبار مرکزی فعال پیدا نشد"));

            List<InventoryStock> centralStocks = inventoryStockRepository
                    .findByVariantsId_VariantIdAndShelvesId_SectionsId_WarehouseIdAndIsActive(
                            variantId, centralWarehouse, true);

            int reservedFromCentral = 0;
            if (!centralStocks.isEmpty()) {
                reservedFromCentral = processStockReservation(
                        variantId, quantity, centralStocks, centralWarehouse, null, newOrder.getOrderId(), userId);
            }

            int remainingQuantity = quantity - reservedFromCentral;
            if (remainingQuantity > 0) {
                List<InventoryStock> otherStocks = inventoryStockRepository
                        .findByVariantsId_VariantIdAndShelvesId_SectionsId_WarehouseIdIsActiveAndNotCentral(
                                variantId);

                otherStocks.sort(Comparator.comparing(InventoryStock::getUpdatedAt));

                for (InventoryStock stock : otherStocks) {
                    if (remainingQuantity <= 0) break;

                    int availableQty = stock.getQuantityOnHand() - stock.getReservedInventory();
                    int qtyToReserve = Math.min(availableQty, remainingQuantity);

                    if (qtyToReserve > 0) {
                        stock.setReservedInventory(stock.getReservedInventory() + qtyToReserve);
                        stock.setQuantityOnHand(stock.getQuantityOnHand() - qtyToReserve);
                        stock.setUpdatedAt(LocalDateTime.now());
                        inventoryStockRepository.save(stock);

                        createInventoryMovement(
                                variantId,
                                stock.getShelvesId().getSectionsId().getWarehouseId(),
                                centralWarehouse,
                                stock.getShelvesId(),
                                null,
                                qtyToReserve,
                                MovementType.ORDER_REQUEST,
                                newOrder.getOrderId(),
                                userId
                        );

                        remainingQuantity -= qtyToReserve;
                    }
                }

                if (remainingQuantity > 0) {
                    throw new RuntimeException("موجودی کافی برای وریانت " + variantId + " وجود ندارد.");
                }
            }

            ProductVariants products = productVariantsRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("محصول پیدا نشد"));

            BigDecimal unitPrice = products.getPrice();
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            Integer discountPercent = discountRepository.findActiveDiscountByVariantAndNow(variantId, LocalDateTime.now())
                    .map(discount -> discount.getDiscountPercentage())
                    .orElse(0);

            BigDecimal discountAmount = subTotal.multiply(BigDecimal.valueOf(discountPercent)).divide(BigDecimal.valueOf(100));
            BigDecimal taxableAmount = subTotal.subtract(discountAmount);
            BigDecimal taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.02));
            BigDecimal totalAmount = taxableAmount.add(taxAmount);

            OrderItem newItem = new OrderItem();
            newItem.setOrderId(newOrder);
            newItem.setVariantId(originalItem.getVariantId());
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(unitPrice);
            newItem.setSubTotal(subTotal);
            newItem.setDiscountAmount(discountAmount);
            newItem.setTaxAmount(taxAmount);
            newItem.setTotalAmount(totalAmount);
            newItem.setCreatedAt(LocalDateTime.now());

            orderItemRepository.save(newItem);

            totalSubTotal = totalSubTotal.add(subTotal);
            totalDiscount = totalDiscount.add(discountAmount);
            totalTax = totalTax.add(taxAmount);
        }

        newOrder.setSubTotal(totalSubTotal);
        newOrder.setDiscountAmount(totalDiscount);
        newOrder.setTaxAmount(totalTax);
        newOrder.setShippingCost(BigDecimal.valueOf(1000000.00));
        newOrder.setTotalAmount(totalSubTotal.subtract(totalDiscount).add(totalTax));
        newOrder.setUpdatedAt(LocalDateTime.now());

        return ordersRepository.save(newOrder);
    }

    private int processStockReservation(UUID variantId, int requiredQuantity,
                                        List<InventoryStock> stocks,
                                        Warehouse warehouse,
                                        Shelves specificShelf,
                                        UUID orderId,
                                        UUID userId) {
        stocks.sort(Comparator.comparing(InventoryStock::getUpdatedAt));

        int totalReserved = 0;

        for (InventoryStock stock : stocks) {
            if (totalReserved >= requiredQuantity) break;
            if (specificShelf != null && !stock.getShelvesId().equals(specificShelf)) continue;

            int availableQty = stock.getQuantityOnHand() - stock.getReservedInventory();
            int qtyToReserve = Math.min(availableQty, requiredQuantity - totalReserved);

            if (qtyToReserve > 0) {
                stock.setReservedInventory(stock.getReservedInventory() + qtyToReserve);
                stock.setQuantityOnHand(stock.getQuantityOnHand() - qtyToReserve);
                stock.setUpdatedAt(LocalDateTime.now());
                inventoryStockRepository.save(stock);

                createInventoryMovement(
                        variantId,
                        warehouse,
                        null,
                        stock.getShelvesId(),
                        null,
                        qtyToReserve,
                        MovementType.ORDER,
                        orderId,
                        userId
                );

                totalReserved += qtyToReserve;
            }
        }

        return totalReserved;
    }

    private void createInventoryMovement(UUID variantId,
                                         Warehouse fromWarehouse,
                                         Warehouse toWarehouse,
                                         Shelves fromShelf,
                                         Shelves toShelf,
                                         int quantity,
                                         MovementType movementType,
                                         UUID referenceId,
                                         UUID userId) {
        InventoryMovement movement = new InventoryMovement();

        ProductVariants variant = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        movement.setVariantsId(variant);
        movement.setFromWarehouseId(fromWarehouse);
        movement.setToWarehouseId(toWarehouse);
        movement.setFromShelvesId(fromShelf);
        movement.setToShelvesId(toShelf);
        movement.setQuantity(quantity);
        movement.setMovementType(movementType);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setRefrenceId(referenceId);

        if (userId != null) {
            UserAccounts user = userAccountsRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            movement.setCreatedBy(user);
        }

        inventoryMovementRepository.save(movement);
    }

    public List<Orders> getProcessingOrdersWithCompleteOrderMovements() {
        List<Orders> orders = ordersRepository.findProcessingOrdersWithCompleteInventoryMovements();
        return orders;
    }

    public List<InventoryMovement> findOrderRequestMovementsByFromWarehouse(UUID warehouseId) {
        // یافتن انبار
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("انبار با شناسه " + warehouseId + " یافت نشد"));

        // استفاده از متد ریپازیتوری
        return inventoryMovementRepository.findByFromWarehouseIdAndMovementType(
                warehouse,
                MovementType.ORDER_REQUEST
        );
    }

    private boolean hasAllItemsInInventoryMovement(Orders order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order);
        if (items.isEmpty()) {
            return false;
        }

        for (OrderItem item : items) {
            long movementCount = inventoryMovementRepository.countByMovementTypeAndRefrenceIdAndVariantsId(
                    MovementType.ORDER,
                    order.getOrderId(),
                    item.getVariantId()
            );

            if (movementCount == 0) {
                return false;
            }
        }

        return true;
    }

    public void confirmOrderSale(UUID orderId,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }
        List<InventoryMovement> movements = inventoryMovementRepository.findByRefrenceId(orderId);

        if (movements.isEmpty()) {
            throw new RuntimeException("هیچ رکوردی برای این سفارش یافت نشد");
        }

        boolean allAreOrder = movements.stream()
                .allMatch(m -> m.getMovementType() == MovementType.ORDER);

        if (!allAreOrder) {
            throw new RuntimeException("تمام رکوردهای مربوط به سفارش باید وضعیت سفارش داشته باشند");
        }

        for (InventoryMovement movement : movements) {
            updateReservedInventory(
                    movement.getVariantsId(),
                    movement.getFromShelvesId(),
                    movement.getQuantity()
            );

            movement.setMovementType(MovementType.SALE_OUT);
            inventoryMovementRepository.save(movement);
        }

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("سفارش یافت نشد"));
        order.setOrderStatus(OrderStatus.delivered);
        ordersRepository.save(order);
    }

    private void updateReservedInventory(ProductVariants variant, Shelves shelf, int quantity) {
        InventoryStock stock = inventoryStockRepository
                .findByVariantsIdAndShelvesId(variant, shelf)
                .orElseThrow(() -> new RuntimeException("موجودی یافت نشد"));

        if (stock.getReservedInventory() < quantity) {
            throw new RuntimeException("موجودی رزرو شده کافی نیست");
        }

        stock.setReservedInventory(stock.getReservedInventory() - quantity);
        inventoryStockRepository.save(stock);
    }

    @Transactional
    public void processOrder(UUID movementId ,String token) {
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        InventoryMovement movement = inventoryMovementRepository.findById(movementId)
                .orElseThrow(() -> new RuntimeException("Inventory Movement not found"));

        if (movement.getMovementType() != MovementType.ORDER_REQUEST) {
            throw new IllegalStateException("شما مجوز لازم برای انجام این انتقال رو ندارید!");
        }

        InventoryStock stock = inventoryStockRepository
                .findByShelvesIdAndVariantsId(movement.getFromShelvesId(), movement.getVariantsId())
                .orElseThrow(() -> new RuntimeException("موجودی رزور شده ای برای این انتقال یافت نشد !"));

        if (stock.getReservedInventory() < movement.getQuantity()) {
            throw new IllegalStateException("موجودی رزرو شده ناکافی هست !");
        }
        movement.setMovementType(MovementType.ORDER);
        inventoryMovementRepository.save(movement);
        inventoryStockRepository.save(stock);
    }

    public void cancelOrder(UUID referenceId){
        List<InventoryMovement> movements = inventoryMovementRepository.findByRefrenceId(referenceId);

        if (movements.isEmpty()) {
            throw new IllegalArgumentException("هیچ رکوردی با این ایدی سفارش پیدا نشد");
        }

        LocalDateTime now = LocalDateTime.now();

        for (InventoryMovement movement : movements) {

            if (movement.getCreatedAt().isBefore(now.minusDays(10))) {
                throw new IllegalStateException("بیش از 10 روز از ایجاد این رکورد گذشته است");
            }

            if (movement.getToWarehouseId() != null) {
                if (!movement.getMovementType().equals(MovementType.ORDER_REQUEST)) {
                    throw new IllegalStateException("امکان لغو سفارش وجود ندارد");
                }
            } else {
                if (!movement.getMovementType().equals(MovementType.ORDER)) {
                    throw new IllegalStateException("امکان لغو سفارش وجود ندارد ");
                }
            }
        }

        for (InventoryMovement movement : movements) {
            InventoryStock stock = inventoryStockRepository.findByVariantsIdAndShelvesId(
                    movement.getVariantsId(), movement.getFromShelvesId()
            ).orElseThrow(() -> new RuntimeException("موجودی برای این رکورد یافت نشد"));

            if (stock.getReservedInventory() < movement.getQuantity()) {
                throw new IllegalStateException("موجودی رزرو شده کافی نیست");
            }

            stock.setReservedInventory(stock.getReservedInventory() - movement.getQuantity());
            stock.setQuantityOnHand(stock.getQuantityOnHand() + movement.getQuantity());
            inventoryStockRepository.save(stock);
        }

        for (InventoryMovement movement : movements) {
            movement.setMovementType(MovementType.CANCEL_ORDER);
            inventoryMovementRepository.save(movement);
        }

        Orders orders = ordersRepository.findById(referenceId).orElseThrow(() -> new RuntimeException("سفارش مورد نظر یافت نشد !"));
        orders.setOrderStatus(OrderStatus.Cancel);
        ordersRepository.save(orders);
    }





//    public Orders addOrder(Orders order, UUID billingAddressId, String token) {
//
//        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
//        String role = JwtUtil.extractUserRole(token);
//
//        if(!customerAddressRepository.existsById(billingAddressId)) {
//            throw new IllegalArgumentException("آدرس مورد نظر یافت نشد");
//        }
//
//        CustomerAddress customerAddress = customerAddressRepository.findById(billingAddressId)
//                .orElseThrow(() -> new RuntimeException("آدرس پیدا نشد"));
//
//        if(!customerRepository.existsByUserId_Id(userId)) {
//            throw new IllegalArgumentException("خریدار مورد نظر یافت نشد");
//        }
//        Customer customer = customerRepository.findByUserId_Id(userId)
//                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));
//
//
//        order.setCustomerId(customer);
//        order.setBillingAddressId(customerAddress);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//        order.setOrderNumber(generateUniqueOrderNumber());
//
//        return ordersRepository.save(order);
//    }

    private String generateUniqueOrderNumber() {
        String orderNumber;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        do {
            String datePart = LocalDate.now().format(formatter);
            int randomPart = (int) (Math.random() * 9000) + 1000;
            orderNumber = "ORD-" + datePart + "-" + randomPart;
        } while (ordersRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }

//    public Orders updateOrder(UUID orderId,Orders updatedOrder, UUID billingAddressId, String token) {
//
//        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
//        String role = JwtUtil.extractUserRole(token);
//
////        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
////            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
////        }
//
//        if(!customerAddressRepository.existsById(billingAddressId)) {
//            throw new IllegalArgumentException("آدرس مورد نظر یافت نشد");
//        }
//
//        CustomerAddress customerAddress = customerAddressRepository.findById(billingAddressId)
//                .orElseThrow(() -> new RuntimeException("آدرس پیدا نشد"));
//
//        if(!customerRepository.existsByUserId_Id(userId)) {
//            throw new IllegalArgumentException("خریدار مورد نظر یافت نشد");
//        }
//        Customer customer = customerRepository.findByUserId_Id(userId)
//                .orElseThrow(() -> new RuntimeException("خریدار پیدا نشد"));
//
//
//        Orders existing = ordersRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("سفارش مورد نظر پیدا نشد"));
//
//
//        existing.setBillingAddressId(customerAddress);
//        existing.setNotes(updatedOrder.getNotes());
//        existing.setDiscountAmount(updatedOrder.getDiscountAmount());
//        existing.setShippingCost(updatedOrder.getShippingCost());
//        existing.setTotalAmount(updatedOrder.getTotalAmount());
//        existing.setTaxAmount(updatedOrder.getTaxAmount());
//        existing.setSubTotal(updatedOrder.getSubTotal());
//        existing.setUpdatedAt(LocalDateTime.now());
//
//        return ordersRepository.save(existing);
//    }

    public Orders getOrderById(UUID orderId) {
        return ordersRepository.findById(orderId).orElseThrow(()-> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !"));
    }

    public Page<Orders> findOrdersByfilter(
            BigDecimal minTotalAmount,
            BigDecimal maxTotalAmount,
            UUID customerId,
            String orderNumber,
            UUID billingAddressId,
            Pageable pageable) {
        return ordersRepository.findAll(
                OrderSpecifications.filterOrders(
                        minTotalAmount,
                        maxTotalAmount,
                        customerId,
                        orderNumber,
                        billingAddressId
                ),
                pageable
        );
    }

//    public void deleteOrder(UUID orderId,String token) {
//        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
//        String role = JwtUtil.extractUserRole(token);
//
//        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("staff")) {
//            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
//        }
//
//        if (!ordersRepository.existsById(orderId)) {
//            throw new IllegalArgumentException("سفارش مورد نظر یافت نشد.");
//        }
//        Orders delete = ordersRepository.findById(orderId).orElseThrow((() -> new EntityNotFoundException("سفارش مورد نظر پیدا نشد !")));
//        ordersRepository.delete(delete);
//     }

    public List<Products> getTopSellingActiveProducts() {
        return inventoryMovementRepository.findTopSellingActiveProducts();
    }

}
