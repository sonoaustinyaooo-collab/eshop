package com.example.demo.model;  // 定義此類別所在的套件（package）

import javax.persistence.*;  // 引入 JPA 的所有註解，用於 ORM 對應
import java.math.BigDecimal;  // 引入 BigDecimal 類別，用於精確的金額計算
import java.util.ArrayList;  // 引入 ArrayList，用於存放訂單項目清單
import java.util.Date;  // 引入 Date 類別，用於記錄日期時間
import java.util.List;  // 引入 List 介面

/**
 * 訂單實體類別
 * 記錄客戶的購買訂單資訊
 * 對應資料庫中的 orders 資料表
 */
@Entity  // 標示這是一個 JPA 實體類別，會對應到資料庫的資料表
@Table(name = "orders")  // 指定對應的資料表名稱為 "orders"
public class Order {  // 定義 Order 類別

    // ========== 主鍵欄位 ==========
    @Id  // 標示此欄位為主鍵（Primary Key）
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主鍵自動遞增，使用資料庫的 AUTO_INCREMENT
    @Column(name = "order_id")  // 對應資料表的欄位名稱為 "order_id"
    private Long orderId;  // 訂單 ID（內部使用的唯一識別碼）

    // ========== 訂單編號欄位 ==========
    @Column(name = "order_number", unique = true, nullable = false)  
    // name = "order_number": 對應資料表的欄位名稱
    // unique = true: 此欄位必須是唯一值，不能重複
    // nullable = false: 此欄位不可為 NULL（必填）
    private String orderNumber;  // 訂單編號（對外顯示，例如：ORD20250101001）

    // ========== 客戶關聯欄位 ==========
    @ManyToOne  // 多對一關聯：多個訂單可以屬於同一個客戶
    @JoinColumn(name = "cust_num", nullable = false)  
    // name = "cust_num": 外鍵欄位名稱
    // nullable = false: 訂單必須有客戶，不可為 NULL
    private Customer customer;  // 訂單所屬的客戶物件

    // ========== 訂單項目關聯欄位 ==========
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    // @OneToMany: 一對多關聯，一個訂單可以有多個訂單項目
    // mappedBy = "order": 關聯由 OrderItem 類別中的 "order" 屬性維護
    // cascade = CascadeType.ALL: 級聯操作，當訂單被刪除時，相關的訂單項目也會被刪除
    // orphanRemoval = true: 當訂單項目從清單中移除時，也會從資料庫刪除
    private List<OrderItem> orderItems = new ArrayList<>();  
    // 初始化為空的 ArrayList，避免 NullPointerException

    // ========== 訂單總金額欄位 ==========
    @Column(name = "total_amount", nullable = false)  // 對應欄位 "total_amount"，不可為 NULL
    private BigDecimal totalAmount;  // 訂單總金額（使用 BigDecimal 確保精確度）

    // ========== 訂單狀態欄位 ==========
    @Column(name = "order_status", nullable = false)  // 對應欄位 "order_status"，不可為 NULL
    @Enumerated(EnumType.STRING)  
    // 將枚舉類型以字串形式儲存到資料庫（例如："PENDING", "SHIPPED"）
    // 如果使用 EnumType.ORDINAL 則會儲存數字（0, 1, 2...），但不建議，因為順序改變會出問題
    private OrderStatus orderStatus;  // 訂單狀態（使用枚舉類型）

    // ========== 收貨人資訊欄位 ==========
    @Column(name = "recipient_name", nullable = false)  // 收貨人姓名，必填
    private String recipientName;  // 收貨人姓名

    @Column(name = "recipient_phone", nullable = false)  // 收貨人電話，必填
    private String recipientPhone;  // 收貨人電話

    @Column(name = "shipping_address", nullable = false)  // 收貨地址，必填
    private String shippingAddress;  // 收貨地址

    // ========== 訂單備註欄位 ==========
    @Column(name = "order_note")  // 對應欄位 "order_note"，可為 NULL（選填）
    private String orderNote;  // 訂單備註（客戶可填寫特殊需求）

    // ========== 時間戳記欄位 ==========
    @Column(name = "created_date")  // 對應欄位 "created_date"
    @Temporal(TemporalType.TIMESTAMP)  
    // 指定日期時間的儲存格式為 TIMESTAMP（包含日期和時間）
    // 其他選項：TemporalType.DATE（只有日期）、TemporalType.TIME（只有時間）
    private Date createdDate;  // 訂單建立時間

    @Column(name = "updated_date")  // 對應欄位 "updated_date"
    @Temporal(TemporalType.TIMESTAMP)  // 儲存格式為 TIMESTAMP
    private Date updatedDate;  // 訂單更新時間

    // ========== 建構子 ==========
    
    /**
     * 無參數建構子
     * JPA 規範要求實體類別必須有無參數建構子
     * 用於 Hibernate 建立物件實例
     */
    public Order() {  
        this.createdDate = new Date();  // 自動設定建立時間為當前時間
        this.updatedDate = new Date();  // 自動設定更新時間為當前時間
        this.orderStatus = OrderStatus.PENDING_PAYMENT;  // 預設訂單狀態為「待處理」
    }

    /**
     * 有參數建構子
     * 方便建立訂單時直接設定必要資訊
     * @param customer 客戶物件
     * @param recipientName 收貨人姓名
     * @param recipientPhone 收貨人電話
     * @param shippingAddress 收貨地址
     */
    public Order(Customer customer, String recipientName, String recipientPhone, String shippingAddress) {
        this();  // 呼叫無參數建構子，初始化日期和狀態
        this.customer = customer;  // 設定客戶
        this.recipientName = recipientName;  // 設定收貨人姓名
        this.recipientPhone = recipientPhone;  // 設定收貨人電話
        this.shippingAddress = shippingAddress;  // 設定收貨地址
        this.orderNumber = generateOrderNumber();  // 自動產生訂單編號
    }

    // ========== Getters and Setters ==========
    // Getter 和 Setter 方法讓其他程式可以存取和修改私有屬性
    
    /**
     * 取得訂單 ID
     * @return 訂單 ID
     */
    public Long getOrderId() { 
        return orderId;  // 回傳 orderId 的值
    }
    
    /**
     * 設定訂單 ID
     * @param orderId 訂單 ID
     */
    public void setOrderId(Long orderId) { 
        this.orderId = orderId;  // 將參數值設定給 orderId 屬性
    }

    /**
     * 取得訂單編號
     * @return 訂單編號
     */
    public String getOrderNumber() { 
        return orderNumber;  
    }
    
    /**
     * 設定訂單編號
     * @param orderNumber 訂單編號
     */
    public void setOrderNumber(String orderNumber) { 
        this.orderNumber = orderNumber;  
    }

    /**
     * 取得客戶物件
     * @return 客戶物件
     */
    public Customer getCustomer() { 
        return customer;  
    }
    
    /**
     * 設定客戶物件
     * @param customer 客戶物件
     */
    public void setCustomer(Customer customer) { 
        this.customer = customer;  
    }

    /**
     * 取得訂單項目清單
     * @return 訂單項目清單
     */
    public List<OrderItem> getOrderItems() { 
        return orderItems;  
    }
    
    /**
     * 設定訂單項目清單
     * @param orderItems 訂單項目清單
     */
    public void setOrderItems(List<OrderItem> orderItems) { 
        this.orderItems = orderItems;  
    }

    /**
     * 取得訂單總金額
     * @return 訂單總金額
     */
    public BigDecimal getTotalAmount() { 
        return totalAmount;  
    }
    
    /**
     * 設定訂單總金額
     * @param totalAmount 訂單總金額
     */
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount;  
    }

    /**
     * 取得訂單狀態
     * @return 訂單狀態枚舉
     */
    public OrderStatus getOrderStatus() { 
        return orderStatus;  
    }
    
    /**
     * 設定訂單狀態
     * @param orderStatus 訂單狀態枚舉
     */
    public void setOrderStatus(OrderStatus orderStatus) { 
        this.orderStatus = orderStatus;  
    }

    /**
     * 取得收貨人姓名
     * @return 收貨人姓名
     */
    public String getRecipientName() { 
        return recipientName;  
    }
    
    /**
     * 設定收貨人姓名
     * @param recipientName 收貨人姓名
     */
    public void setRecipientName(String recipientName) { 
        this.recipientName = recipientName;  
    }

    /**
     * 取得收貨人電話
     * @return 收貨人電話
     */
    public String getRecipientPhone() { 
        return recipientPhone;  
    }
    
    /**
     * 設定收貨人電話
     * @param recipientPhone 收貨人電話
     */
    public void setRecipientPhone(String recipientPhone) { 
        this.recipientPhone = recipientPhone;  
    }

    /**
     * 取得收貨地址
     * @return 收貨地址
     */
    public String getShippingAddress() { 
        return shippingAddress;  
    }
    
    /**
     * 設定收貨地址
     * @param shippingAddress 收貨地址
     */
    public void setShippingAddress(String shippingAddress) { 
        this.shippingAddress = shippingAddress;  
    }

    /**
     * 取得訂單備註
     * @return 訂單備註
     */
    public String getOrderNote() { 
        return orderNote;  
    }
    
    /**
     * 設定訂單備註
     * @param orderNote 訂單備註
     */
    public void setOrderNote(String orderNote) { 
        this.orderNote = orderNote;  
    }

    /**
     * 取得建立時間
     * @return 建立時間
     */
    public Date getCreatedDate() { 
        return createdDate;  
    }
    
    /**
     * 設定建立時間
     * @param createdDate 建立時間
     */
    public void setCreatedDate(Date createdDate) { 
        this.createdDate = createdDate;  
    }

    /**
     * 取得更新時間
     * @return 更新時間
     */
    public Date getUpdatedDate() { 
        return updatedDate;  
    }
    
    /**
     * 設定更新時間
     * @param updatedDate 更新時間
     */
    public void setUpdatedDate(Date updatedDate) { 
        this.updatedDate = updatedDate;  
    }

    // ========== 業務方法 ==========
    
    /**
     * 新增訂單項目到訂單中
     * 此方法會建立雙向關聯：訂單包含項目，項目也知道自己屬於哪個訂單
     * @param orderItem 要新增的訂單項目
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);  // 將訂單項目加入到清單中
        orderItem.setOrder(this);  // 設定訂單項目的訂單為當前訂單（建立反向關聯）
    }

    /**
     * 計算訂單總金額
     * 遍歷所有訂單項目，累加每個項目的小計
     */
    public void calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;  // 初始化總金額為 0
        
        // 使用 for-each 迴圈遍歷所有訂單項目
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotal());  // 累加每個項目的小計
            // BigDecimal.add() 會返回新的 BigDecimal 物件，不會修改原物件
        }
        
        this.totalAmount = total;  // 將計算結果設定給 totalAmount 屬性
    }

    /**
     * 產生訂單編號
     * 格式：ORD + 時間戳記
     * 實際專案中應該從資料庫取得流水號，確保唯一性
     * @return 訂單編號字串
     */
    private String generateOrderNumber() {
        // System.currentTimeMillis() 取得當前時間的毫秒數
        // "ORD" + 毫秒數 組合成訂單編號，例如：ORD1704153600000
        return "ORD" + System.currentTimeMillis();
    }

    /**
     * 覆寫 toString() 方法
     * 當需要將 Order 物件轉換成字串時（例如：System.out.println(order)）會呼叫此方法
     * @return 訂單的字串表示
     */
    @Override
    public String toString() {
        // 使用字串串接，組合訂單的主要資訊
        return "Order{" +
                "orderId=" + orderId +  // 訂單 ID
                ", orderNumber='" + orderNumber + '\'' +  // 訂單編號
                ", customer=" + customer.getCustName() +  // 客戶名稱
                ", totalAmount=" + totalAmount +  // 總金額
                ", orderStatus=" + orderStatus +  // 訂單狀態
                ", createdDate=" + createdDate +  // 建立時間
                '}';
    }
}