package com.example.Sanjaghak.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;


@Entity
public class AttributeRequirement {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    @JsonIgnoreProperties({"attributeName", "attributeType", "createdBy", "createdAt"})
    private ProductAttribute attributeId;

    @ManyToOne
    @JoinColumn(name="category_id")
    @JsonIgnoreProperties({"categoryName", "categoryDescription", "createdBy", "createdAt","active","updatedAt","updatedBy"})
    private Categories categoryId;

    private boolean isRequired;

    public ProductAttribute getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(ProductAttribute attributeId) {
        this.attributeId = attributeId;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }
}
