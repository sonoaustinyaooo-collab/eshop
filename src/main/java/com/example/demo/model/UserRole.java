package com.example.demo.model;

/**
 * 使用者角色枚舉
 * 定義系統中的使用者類型
 */
public enum UserRole {
    ADMIN("管理員"),       // 系統管理員，可以管理所有功能
    CUSTOMER("顧客");      // 一般顧客，只能購物和查看自己的訂單

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}