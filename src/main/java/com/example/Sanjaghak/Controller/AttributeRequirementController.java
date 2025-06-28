package com.example.Sanjaghak.Controller;

import com.example.Sanjaghak.Service.AttributeRequirementService;
import com.example.Sanjaghak.model.AttributeRequirement;
import com.example.Sanjaghak.model.ProductAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Sanjaghak/attributeRequirement")
public class AttributeRequirementController {
    @Autowired
    private AttributeRequirementService attributeRequirementService;

    @PostMapping("/addRequirement")
    public ResponseEntity<?> createRequirement(@RequestBody AttributeRequirement attributeRequirement,
                                               @RequestParam UUID categoryId,
                                               @RequestParam UUID attributeId,
                                               @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            AttributeRequirement save = attributeRequirementService.createAttributeRequirement(attributeRequirement, categoryId, attributeId, token);
            return ResponseEntity.ok(save);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAttributeRequirement(@PathVariable UUID id , @RequestBody AttributeRequirement attributeRequirement, @RequestParam UUID categoryId ,
                                                        @RequestParam UUID attributeId,  @RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ", "");
            AttributeRequirement update = attributeRequirementService.updateAttributeRequirement(id, attributeRequirement, categoryId, attributeId, token);
            return ResponseEntity.ok(update);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @GetMapping("/getAllAttributeRequirement")
    public ResponseEntity<?> getAllAttributeRequirement(){
        return ResponseEntity.ok().body(attributeRequirementService.getAllAttributeRequirements());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAttributeRequirement(@PathVariable UUID id){

        try{
            AttributeRequirement get = attributeRequirementService.getAttributeRequirementById(id);
            return ResponseEntity.ok(get);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProductAttributeRequiremenr(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            attributeRequirementService.deleteProductAttributeRequirement(id,token);
            return ResponseEntity.ok().body("ویژگی مورد نظر با موفقیت حذف شد");
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }
    @GetMapping("/{categoryId}/required-attributes")
    public ResponseEntity<?> getRequiredAttributes(
            @PathVariable UUID categoryId
           ) {
        try {

            List<ProductAttribute> requiredAttributes = attributeRequirementService.getRequiredAttributeRequirementByCategory(categoryId);
            return ResponseEntity.ok(requiredAttributes);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("شما مجوز لازم برای انجام این عملیات را ندارید".equals(msg)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", msg));

            } else {
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }
        }
    }

}
