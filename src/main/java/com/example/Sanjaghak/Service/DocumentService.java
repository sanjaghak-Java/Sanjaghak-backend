package com.example.Sanjaghak.Service;

//import com.example.Sanjaghak.Repository.OrderItemRepository;
//import com.example.Sanjaghak.Repository.OrdersRepository;
//import com.example.Sanjaghak.model.OrderItem;
//import com.example.Sanjaghak.model.Orders;
//import com.example.Sanjaghak.model.ProductVariants;
//import com.example.Sanjaghak.model.Products;
//import java.io.IOException;
//
//import com.itextpdf.io.font.FontProgramFactory;
//import com.itextpdf.io.font.PdfEncodings;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.List;


@Service
public class DocumentService {

//    @Autowired
//    private OrderItemRepository orderItemRepository;
//
//    private OrdersRepository ordersRepository;
//
//
//    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,### ریال");
//    private PdfFont persianFont;
//
//    @PostConstruct
//    public void init() {
//        try {
//            InputStream fontStream = getClass().getResourceAsStream("/fonts/Vazirmatn-Medium.ttf");
//            if (fontStream == null) {
//                throw new RuntimeException("فونت فارسی در مسیر /fonts/Vazirmatn-Medium.ttf یافت نشد");
//            }
//
//            byte[] fontBytes = fontStream.readAllBytes();
//
//            persianFont = PdfFontFactory.createFont(
//                    FontProgramFactory.createFont(fontBytes),
//                    PdfEncodings.IDENTITY_H
//            );
//
//        } catch (Exception e) { // تغییر از IOException به Exception
//            throw new RuntimeException("خطا در بارگذاری فونت فارسی", e);
//        }
//    }
//
//    public byte[] generateOrderItemsPdf(List<OrderItem> orderItems) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
//             Document document = new Document(pdfDoc)) {
//
//            // تنظیم فونت پیش‌فرض برای سند
//            document.setFont(persianFont);
//
//            // عنوان اصلی با فونت فارسی
//            Paragraph title = new Paragraph("لیست اقلام سفارش")
//                    .setFont(persianFont)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFontSize(16)
//                    .setBold();
//            document.add(title);
//
//            // ایجاد جدول
//            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 2, 2}));
//            table.setWidth(UnitValue.createPercentValue(100));
//
//            // هدرهای جدول با فونت فارسی
//            addTableHeaders(table);
//
//            // اضافه کردن اقلام سفارش
//            addOrderItemsToTable(orderItems, table);
//
//            document.add(table);
//
//        } catch (Exception  e) {
//            throw new RuntimeException("خطا در تولید فایل PDF", e);
//        }
//
//        return baos.toByteArray();
//    }
//
//    private void addTableHeaders(Table table) {
//        table.addHeaderCell(createPersianCell("نام محصول", true));
//        table.addHeaderCell(createPersianCell("رنگ", true));
//        table.addHeaderCell(createPersianCell("قیمت واحد", true));
//        table.addHeaderCell(createPersianCell("تعداد", true));
//        table.addHeaderCell(createPersianCell("تخفیف", true));
//        table.addHeaderCell(createPersianCell("مالیات", true));
//        table.addHeaderCell(createPersianCell("قیمت نهایی", true));
//    }
//
//    private void addOrderItemsToTable(List<OrderItem> orderItems, Table table) {
//        for (OrderItem item : orderItems) {
//            ProductVariants variant = item.getVariantId();
//            Products product = variant.getProductId();
//
//            table.addCell(createPersianCell(product.getProductName(), false));
//            table.addCell(createPersianCell(variant.getColor(), false));
//            table.addCell(createPersianCell(formatMoney(item.getUnitPrice()), false));
//            table.addCell(createPersianCell(String.valueOf(item.getQuantity()), false));
//            table.addCell(createPersianCell(formatMoney(item.getDiscountAmount()), false));
//            table.addCell(createPersianCell(formatMoney(item.getTaxAmount()), false));
//            table.addCell(createPersianCell(formatMoney(item.getTotalAmount()), false));
//        }
//    }
//
//    private Cell createPersianCell(String text, boolean isHeader) {
//        Cell cell = new Cell()
//                .add(new Paragraph(text)
//                        .setFont(persianFont)
//                        .setTextAlignment(TextAlignment.RIGHT));
//
//        if (isHeader) {
//            cell.setBackgroundColor(new DeviceRgb(200, 200, 200))
//                    .setBold();
//        }
//
//        return cell;
//    }
//
//    private String formatMoney(BigDecimal amount) {
//        if (amount == null) return "۰ ریال";
//
//        DecimalFormat formatter = new DecimalFormat("#,###");
//        String formatted = formatter.format(amount);
//
//        // تبدیل اعداد انگلیسی به فارسی
//        formatted = formatted.replace('0', '۰')
//                .replace('1', '۱')
//                .replace('2', '۲')
//                .replace('3', '۳')
//                .replace('4', '۴')
//                .replace('5', '۵')
//                .replace('6', '۶')
//                .replace('7', '۷')
//                .replace('8', '۸')
//                .replace('9', '۹');
//
//        return formatted + " ریال";
//    }
}
