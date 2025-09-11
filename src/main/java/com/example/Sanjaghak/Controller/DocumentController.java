package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Repository.OrderItemRepository;

import com.example.Sanjaghak.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/Document")
public class DocumentController {

//    @Autowired
//    private DocumentService documentService;
//
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//
//    @GetMapping("export-order-pdf/{orderId}")
//    public ResponseEntity<byte[]> generateOrderItemsPdf(@PathVariable UUID orderId) {
//        List<OrderItem> orderItems = orderItemRepository.findByOrderId_OrderId(orderId);
//        byte[] pdfBytes = documentService.generateOrderItemsPdf(orderItems);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDisposition(ContentDisposition.builder("attachment")
//                .filename("order_" + orderId + "_items.pdf").build());
//
//        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//    }


}
