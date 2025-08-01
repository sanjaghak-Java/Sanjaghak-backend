package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/Sanjaghak/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/received-purchase-quantity")
    public ResponseEntity<Integer> getReceivedQuantity(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Integer totalReceivedQuantity = reportService.getReceivedQuantityBetweenDates(startDate, endDate);
        return ResponseEntity.ok(totalReceivedQuantity);
    }

    @GetMapping("/getPurchaseOrdersSummary")
    public ResponseEntity<Map<String, BigDecimal>> getPurchaseOrdersSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(reportService.getPurchaseOrdersSummary(startDate, endDate));
    }

    @GetMapping("/delivered-summary")
    public ResponseEntity<Map<String, Object>> getDeliveredOrdersSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(reportService.getDeliveredOrdersSummary(startDate, endDate));
    }

    @GetMapping("/returns-summary")
    public ResponseEntity<Map<String, Object>> getReturnSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> summary = reportService.getReturnSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/calculate")
    public BigDecimal calculateProfit(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return reportService.calculateProfit(startDate, endDate);
    }

}
