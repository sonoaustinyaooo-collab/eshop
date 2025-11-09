package com.example.demo.model;  

/**
 * 訂單狀態枚舉
 * 枚舉（Enum）是一種特殊的類別，用於定義一組固定的常數
 * 用於表示訂單的各種狀態
 */
public enum OrderStatus {  
	    
    // 定義六個枚舉常數，每個代表一種訂單狀態
    // 格式：常數名稱("顯示名稱")
	PENDING_PAYMENT("待付款"),      // 訂單已建立，但尚未開始處理
	PAID("已付款"),            // 訂單已付款，等待處理
    PROCESSING("處理中"),   // 訂單正在處理中（例如：包裝、準備出貨）
    SHIPPED("已出貨"),      // 訂單已經出貨，正在運送中
    DELIVERED("已送達"),    // 訂單已經送達客戶手中
    CANCELLED("已取消");    // 訂單已被取消

    // 枚舉的私有屬性，用於儲存狀態的中文顯示名稱
    private final String displayName;

    /**
     * 枚舉的建構子
     * 枚舉建構子必須是 private 或 package-private（預設）
     * 外部無法直接 new OrderStatus()
     * @param displayName 狀態的顯示名稱
     */
    OrderStatus(String displayName) {
        this.displayName = displayName;  // 將傳入的顯示名稱儲存到屬性中
    }

    /**
     * 取得狀態的顯示名稱
     * @return 中文顯示名稱
     */
    public String getDisplayName() {
        return displayName;  // 回傳顯示名稱
    }
}