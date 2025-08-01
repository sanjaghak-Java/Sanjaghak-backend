package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.OrderStatus;
import com.example.Sanjaghak.Enum.Statuses;
import com.example.Sanjaghak.Repository.*;
import com.example.Sanjaghak.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

    @Autowired
    private PurchaseOrdersRepository purchaseOrdersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private ProductVariantsRepository productVariantsRepository;

    public Integer getReceivedQuantityBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Integer totalReceived = purchaseOrderItemsRepository.getTotalReceivedQuantity(startDate, endDate);
        return totalReceived != null ? totalReceived : 0;
    }

    public Map<String, BigDecimal> getPurchaseOrdersSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Object[] result = purchaseOrdersRepository.getOrdersSummary(startDate, endDate).get(0);

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("subTotal", (BigDecimal) result[0]);
        summary.put("shippingCost", (BigDecimal) result[1]);
        summary.put("taxAmount", (BigDecimal) result[2]);
        summary.put("totalAmount", (BigDecimal) result[3]);

        return summary;
    }

    public Map<String, Object> getDeliveredOrdersSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Integer totalQuantity = orderItemRepository.getTotalQuantity(startDate, endDate);
        Object[] orderSums = ordersRepository.getOrderSums(startDate, endDate);

        Map<String, Object> summary = new HashMap<>();

        summary.put("quantity", totalQuantity != null ? totalQuantity : 0);

        if (orderSums != null && orderSums.length > 0) {
            Object[] sums = (Object[]) orderSums[0];
            summary.put("subTotal", sums.length > 0 ? sums[0] : BigDecimal.ZERO);
            summary.put("shippingCost", sums.length > 1 ? sums[1] : BigDecimal.ZERO);
            summary.put("taxAmount", sums.length > 2 ? sums[2] : BigDecimal.ZERO);
            summary.put("discountAmount", sums.length > 3 ? sums[3] : BigDecimal.ZERO);
            summary.put("totalAmount", sums.length > 4 ? sums[4] : BigDecimal.ZERO);
        } else {
            summary.put("subTotal", BigDecimal.ZERO);
            summary.put("shippingCost", BigDecimal.ZERO);
            summary.put("taxAmount", BigDecimal.ZERO);
            summary.put("discountAmount", BigDecimal.ZERO);
            summary.put("totalAmount", BigDecimal.ZERO);
        }

        return summary;
    }

    public Map<String, Object> getReturnSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> returnData = returnItemRepository.findReturnSummaryBetweenDates(startDate, endDate);

        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Object[] row : returnData) {
            Number qty = (Number) row[1];
            Number price = (Number) row[2];
            Number discount = (Number) row[3];

            totalQuantity += qty != null ? qty.intValue() : 0;
            totalPrice = totalPrice.add(price != null ? BigDecimal.valueOf(price.doubleValue()) : BigDecimal.ZERO);
            totalDiscount = totalDiscount.add(discount != null ? BigDecimal.valueOf(discount.doubleValue()) : BigDecimal.ZERO);
        }

        BigDecimal totalAmount = totalPrice.subtract(totalDiscount);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuantity", totalQuantity);
        summary.put("totalPrice", totalPrice);
        summary.put("totalDiscount", totalDiscount);
        summary.put("totalAmount", totalAmount);

        return summary;
    }

    public BigDecimal calculateProfit(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalProfit = BigDecimal.ZERO;

        List<Orders> deliveredOrders = ordersRepository.findByOrderStatusAndCreatedAtBetween(
                OrderStatus.delivered, startDate, endDate);

        for (Orders order : deliveredOrders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order);

            for (OrderItem item : orderItems) {
                BigDecimal itemProfit = calculateItemProfit(item, startDate);
                totalProfit = totalProfit.add(itemProfit);
            }
        }

        return totalProfit;
    }

    private BigDecimal calculateItemProfit(OrderItem item, LocalDateTime startDate) {
        int previousSoldQuantity = calculatePreviousSoldQuantity(item.getVariantId(), startDate);

        List<PurchaseOrderItems> receivedItems = purchaseOrderItemsRepository
                .findReceivedItemsByVariant(item.getVariantId(), Statuses.received);

        BigDecimal purchaseCost = BigDecimal.ZERO;
        int remainingQuantity = item.getQuantity();
        int quantityToCover = previousSoldQuantity + item.getQuantity();

        for (PurchaseOrderItems poItem : receivedItems) {
            if (quantityToCover <= 0) break;

            int availableQuantity = poItem.getRecivedQuantity();
            int usedQuantity = Math.min(availableQuantity, quantityToCover);

            if (remainingQuantity > 0) {
                int quantityForCurrentItem = Math.min(usedQuantity, remainingQuantity);
                purchaseCost = purchaseCost.add(
                        poItem.getUnitPrice().multiply(BigDecimal.valueOf(quantityForCurrentItem)));
                remainingQuantity -= quantityForCurrentItem;
            }

            quantityToCover -= usedQuantity;
        }

        BigDecimal saleRevenue = item.getSubTotal().subtract(item.getDiscountAmount());
        return saleRevenue.subtract(purchaseCost);
    }

    private int calculatePreviousSoldQuantity(ProductVariants variant, LocalDateTime beforeDate) {
        List<OrderItem> previousItems = orderItemRepository
                .findByVariantIdAndOrderIdCreatedAtBefore(variant, beforeDate);

        return previousItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }








}
