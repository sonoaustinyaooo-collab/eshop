package com.example.demo.model;  // 定義套件

import javax.persistence.*;  // 引入 JPA 註解
import java.math.BigDecimal;  // 引入 BigDecimal 用於金額計算

/**
 * 訂單明細實體類別
 * 記錄訂單中每個產品的詳細資訊
 * 每一筆 OrderItem 代表訂單中的一個商品項目
 * 對應資料庫中的 order_items 資料表
 */
@Entity  // 標示這是 JPA 實體類別
@Table(name = "order_items")  // 對應資料表名稱為 "order_items"
public class OrderItem {  // 定義 OrderItem 類別

    // ========== 主鍵欄位 ==========
    @Id  // 標示為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主鍵自動遞增
    @Column(name = "order_item_id")  // 對應欄位名稱
    private Long orderItemId;  // 訂單項目 ID

    // ========== 訂單關聯欄位 ==========
    @ManyToOne  // 多對一關聯：多個訂單項目屬於一個訂單
    @JoinColumn(name = "order_id", nullable = false)  
    // name = "order_id": 外鍵欄位名稱
    // nullable = false: 訂單項目必須屬於某個訂單
    private Order order;  // 此項目所屬的訂單物件

    // ========== 產品關聯欄位 ==========
    @ManyToOne  // 多對一關聯：多個訂單項目可以引用同一個產品
    @JoinColumn(name = "prod_num", nullable = false)  
    // name = "prod_num": 外鍵欄位名稱
    // nullable = false: 訂單項目必須有產品
    private Product product;  // 此項目對應的產品物件

    // ========== 產品名稱快照欄位 ==========
    @Column(name = "product_name", nullable = false)  
    // 儲存購買時的產品名稱
    // 為什麼要儲存快照？
    // 1. 避免產品被改名後，歷史訂單顯示錯誤
    // 2. 避免產品被刪除後，訂單無法顯示產品名稱
    private String productName;  // 產品名稱快照

    // ========== 購買數量欄位 ==========
    @Column(name = "quantity", nullable = false)  // 對應欄位 "quantity"，必填
    private Integer quantity;  // 購買數量（例如：購買 3 件）

    // ========== 單價欄位 ==========
    @Column(name = "unit_price", nullable = false)  // 對應欄位 "unit_price"，必填
    // 為什麼要儲存購買時的價格？
    // 因為產品價格可能會變動，但已下訂的訂單不應該受影響
    // 例如：今天買 iPhone 30000 元，明天降價到 25000 元，我的訂單還是 30000 元
    private BigDecimal unitPrice;  // 購買時的單價快照

    // ========== 建構子 ==========
    
    /**
     * 無參數建構子
     * JPA 要求必須有無參數建構子
     */
    public OrderItem() {
        // 空建構子，由 JPA 使用
    }

    /**
     * 有參數建構子
     * 建立訂單項目時，自動從產品物件複製名稱和價格
     * @param product 產品物件
     * @param quantity 購買數量
     */
    public OrderItem(Product product, Integer quantity) {
        this.product = product;  // 設定產品物件
        this.productName = product.getProdName();  // 複製產品名稱（建立快照）
        this.quantity = quantity;  // 設定購買數量
        this.unitPrice = product.getProdPrice();  // 複製當前價格（建立價格快照）
    }

    // ========== Getters and Setters ==========
    
    /**
     * 取得訂單項目 ID
     * @return 訂單項目 ID
     */
    public Long getOrderItemId() { 
        return orderItemId;  
    }
    
    /**
     * 設定訂單項目 ID
     * @param orderItemId 訂單項目 ID
     */
    public void setOrderItemId(Long orderItemId) { 
        this.orderItemId = orderItemId;  
    }

    /**
     * 取得所屬的訂單物件
     * @return 訂單物件
     */
    public Order getOrder() { 
        return order;  
    }
    
    /**
     * 設定所屬的訂單物件
     * @param order 訂單物件
     */
    public void setOrder(Order order) { 
        this.order = order;  
    }

    /**
     * 取得產品物件
     * @return 產品物件
     */
    public Product getProduct() { 
        return product;  
    }
    
    /**
     * 設定產品物件
     * @param product 產品物件
     */
    public void setProduct(Product product) { 
        this.product = product;  
    }

    /**
     * 取得產品名稱快照
     * @return 產品名稱
     */
    public String getProductName() { 
        return productName;  
    }
    
    /**
     * 設定產品名稱快照
     * @param productName 產品名稱
     */
    public void setProductName(String productName) { 
        this.productName = productName;  
    }

    /**
     * 取得購買數量
     * @return 購買數量
     */
    public Integer getQuantity() { 
        return quantity;  
    }
    
    /**
     * 設定購買數量
     * @param quantity 購買數量
     */
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;  
    }

    /**
     * 取得單價
     * @return 單價
     */
    public BigDecimal getUnitPrice() { 
        return unitPrice;  
    }
    
    /**
     * 設定單價
     * @param unitPrice 單價
     */
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice;  
    }

    // ========== 業務方法 ==========
    
    /**
     * 計算小計（單價 × 數量）
     * 例如：單價 1000 元，數量 3，小計 = 3000 元
     * @return 小計金額
     */
    public BigDecimal getSubtotal() {
        // unitPrice.multiply(x) 是 BigDecimal 的乘法方法
        // BigDecimal.valueOf(quantity) 將 Integer 轉換為 BigDecimal
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 覆寫 toString() 方法
     * 提供訂單項目的字串表示
     * @return 訂單項目的字串描述
     */
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +  // 項目 ID
                ", productName='" + productName + '\'' +  // 產品名稱
                ", quantity=" + quantity +  // 數量
                ", unitPrice=" + unitPrice +  // 單價
                ", subtotal=" + getSubtotal() +  // 小計（呼叫 getSubtotal() 方法計算）
                '}';
    }
}