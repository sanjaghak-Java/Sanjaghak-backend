package com.example.Sanjaghak.Repository;

import com.example.Sanjaghak.model.ProductAttribute;
import com.example.Sanjaghak.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID>, JpaSpecificationExecutor<ProductAttribute> {

}
