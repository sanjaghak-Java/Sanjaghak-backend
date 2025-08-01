package com.example.Sanjaghak.Service;

import com.example.Sanjaghak.Enum.ReturnStatus;
import com.example.Sanjaghak.Repository.OrderItemRepository;
import com.example.Sanjaghak.Repository.ReturnItemRepository;
import com.example.Sanjaghak.Repository.ReturnRepository;
import com.example.Sanjaghak.model.OrderItem;
import com.example.Sanjaghak.model.Return;
import com.example.Sanjaghak.model.ReturnItem;
import com.example.Sanjaghak.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReturnItemService {
    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public ReturnItem addToReturn(UUID returnId,UUID orderItemId,ReturnItem returnItem,String token){

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        Return curentReturn = returnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("سفارش مرجوعی یافت نشد !"));

        if(!curentReturn.getReturnStatus().equals(ReturnStatus.PENDING)){
            throw new RuntimeException("شما نمی توانید به این درخواست مرجوعی ایتم اضافه کنید! ");
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("ایتم سفارش یافت نشد !"));

        if(!orderItem.getOrderId().equals(curentReturn.getOrderId())){
            throw new RuntimeException("نمی توان از سفارش دیگر در خواست مرجوعی داد !");
        }

        if(returnItem.getQuantity()>orderItem.getQuantity()){
            throw new RuntimeException("نمی توان بیشتر از تعداد سفارش درخواست مرجوعی داد!");
        }

        returnItem.setReturnId(curentReturn);
        returnItem.setOrderItemId(orderItem);
        return returnItemRepository.save(returnItem);
    }

    public ReturnItem getReturnItemById(UUID ReturnItemId) {
        return returnItemRepository.findById(ReturnItemId).orElseThrow(()-> new EntityNotFoundException("ایتم مرجوعی مورد نظر پیدا نشد !"));
    }

    public List<ReturnItem> getAllReturnItem() {
        return returnItemRepository.findAll();
    }

    public List<ReturnItem> getReturnItemByReturnId(UUID returnId) {
        Return curentReturn = returnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("سفارش مرجوعی یافت نشد !"));
        List<ReturnItem> returnItems = returnItemRepository.findByReturnId(curentReturn);
        return returnItems;
    }

    public ReturnItem update(UUID returnItemId,ReturnItem returnItemUpdate,String token){

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ReturnItem returnItem = returnItemRepository.findById(returnItemId)
                .orElseThrow(() -> new RuntimeException("ایتم سفارش مرجوعی یافت نشد !"));

        Return curentReturn = returnRepository.findById(returnItem.getReturnId().getReturnId())
                .orElseThrow(() -> new RuntimeException("سفارش مرجوعی یافت نشد !"));

        if(!curentReturn.getReturnStatus().equals(ReturnStatus.PENDING)){
            throw new RuntimeException("شما نمی توانید ایتم مرجوعی را در این وضعیت ویرایش دهید! ");
        }

        OrderItem orderItem = orderItemRepository.findById(returnItem.getOrderItemId().getOrderItemId())
                .orElseThrow(() -> new RuntimeException("ایتم سفارش یافت نشد !"));

        if(!orderItem.getOrderId().equals(curentReturn.getOrderId())){
            throw new RuntimeException("نمی توان از سفارش دیگر در خواست مرجوعی داد !");
        }

        if(returnItemUpdate.getQuantity()>orderItem.getQuantity()){
            throw new RuntimeException("نمی توان بیشتر از تعداد سفارش درخواست مرجوعی داد!");
        }

        returnItem.setQuantity(returnItemUpdate.getQuantity());
        returnItem.setTitle(returnItemUpdate.getTitle());
        returnItem.setDescription(returnItemUpdate.getDescription());
        return returnItemRepository.save(returnItem);
    }

    public void deleteItem (UUID returnItemId,String token){
        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("customer")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ReturnItem returnItem = returnItemRepository.findById(returnItemId)
                .orElseThrow(() -> new RuntimeException("ایتم سفارش مرجوعی یافت نشد !"));

        Return curentReturn = returnRepository.findById(returnItem.getReturnId().getReturnId())
                .orElseThrow(() -> new RuntimeException("سفارش مرجوعی یافت نشد !"));

        if(!curentReturn.getReturnStatus().equals(ReturnStatus.PENDING)){
            throw new RuntimeException("شما نمی توانید ایتم مرجوعی را در این وضعیت حذف کنید! ");
        }

        returnItemRepository.delete(returnItem);
    }

    public ReturnItem checkReturnItem(UUID returnItemId,Boolean isRestock,String token){

        UUID userId = UUID.fromString(JwtUtil.extractUserId(token));
        String role = JwtUtil.extractUserRole(token);

        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            throw new RuntimeException("شما مجوز لازم برای انجام این عملیات را ندارید");
        }

        ReturnItem returnItem = getReturnItemById(returnItemId);

        if(!returnItem.getReturnId().getReturnStatus().equals(ReturnStatus.CHECKING)){
            throw new RuntimeException("شما نمی توانید ایتم مرجوعی را در این وضعیت برسی کنید! ");
        }

        returnItem.setRestock(isRestock);
        return returnItemRepository.save(returnItem);
    }
}
