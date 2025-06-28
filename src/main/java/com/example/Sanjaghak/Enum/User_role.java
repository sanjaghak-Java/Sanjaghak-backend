package com.example.Sanjaghak.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum User_role {
    admin,
    manager,
    staff,
    customer;

    @JsonCreator
    public static User_role fromString(String role) {
        if (role == null) return null;
        for (User_role r : User_role.values()) {
            if (r.name().equalsIgnoreCase(role.trim())) {
                return r;
            }
        }
        throw new IllegalArgumentException("نقش وارد شده معتبر نیست! باید یکی از این‌ها باشد: admin, manager, staff, customer");
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
