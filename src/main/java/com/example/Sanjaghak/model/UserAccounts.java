package com.example.Sanjaghak.model;

import com.example.Sanjaghak.Enum.User_role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class UserAccounts {

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private User_role role = User_role.customer;

    private boolean isActive = true;

    private LocalDateTime  lastLogin;

    private LocalDateTime createdAt ;

    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference("createdByRef")
    private List<Categories> categories;

    @OneToMany(mappedBy = "updatedBy")
    @JsonManagedReference("updatedByRef")
    private List<Categories> category;

    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference("productCreatedByRef")
    private List<Products> createdProducts;

    @OneToMany(mappedBy = "updatedBy")
    @JsonManagedReference("productUpdatedByRef")
    private List<Products> updatedProducts;

    public List<Products> getCreatedProducts() {
        return createdProducts;
    }

    public void setCreatedProducts(List<Products> createdProducts) {
        this.createdProducts = createdProducts;
    }

    public List<Products> getUpdatedProducts() {
        return updatedProducts;
    }

    public void setUpdatedProducts(List<Products> updatedProducts) {
        this.updatedProducts = updatedProducts;
    }

    public List<Categories> getCategory() {
        return category;
    }

    public void setCategory(List<Categories> category) {
        this.category = category;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User_role getRole() {
        return role;
    }

    public void setRole(User_role role) {
        this.role = role;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
