package com.example.demo.service;  // 定義套件

// 引入需要的類別
import com.example.demo.model.Order;  // 訂單實體
import com.example.demo.model.OrderStatus;  // 訂單狀態枚舉
import java.util.List;  

/**
 * 訂單 Service 介面
 * Service 層負責處理業務邏輯
 * 介於 Controller 和 DAO 之間
 * 定義訂單相關的業務邏輯方法
 */
//定義 OrderService 介面
public interface OrderService {  

    /**
     * 從購物車建立訂單
     * 這是最重要的業務方法，將購物車的商品轉換成正式訂單
     * 流程：
     * 1. 取得客戶的購物車
     * 2. 檢查購物車是否為空
     * 3. 建立訂單並複製購物車項目
     * 4. 計算訂單總金額
     * 5. 清空購物車
     * 
     * @param customerId 客戶 ID（哪個客戶下訂）
     * @param recipientName 收貨人姓名（可能不是客戶本人）
     * @param recipientPhone 收貨人電話
     * @param shippingAddress 收貨地址
     * @param orderNote 訂單備註（客戶的特殊需求）
     * @return 建立完成的訂單物件
     */
    Order createOrderFromCart(Long customerId, String recipientName, 
                             String recipientPhone, String shippingAddress, 
                             String orderNote);
    
    /**
     * 根據 ID 查詢訂單
     * 查詢單一訂單的詳細資訊
     * @param orderId 訂單 ID
     * @return 訂單物件
     */
    Order getOrderById(Long orderId);
    
    /**
     * 根據訂單編號查詢
     * 客戶通常使用訂單編號查詢訂單
     * @param orderNumber 訂單編號（例如：ORD20250101001）
     * @return 訂單物件
     */
    Order getOrderByOrderNumber(String orderNumber);
    
    /**
     * 查詢所有訂單
     * 管理員查看所有訂單時使用
     * @return 所有訂單的 List
     */
    List<Order> getAllOrders();
    
    /**
     * 查詢客戶的所有訂單
     * 客戶查看「我的訂單」時使用
     * @param customerId 客戶 ID
     * @return 該客戶的所有訂單 List
     */
    List<Order> getOrdersByCustomerId(Long customerId);
    
    /**
     * 根據狀態查詢訂單
     * 查詢特定狀態的所有訂單
     * 例如：查看所有「待付款」的訂單
     * @param status 訂單狀態枚舉
     * @return 符合該狀態的所有訂單 List
     */
    List<Order> getOrdersByStatus(OrderStatus status);
    
    /**
     * 更新訂單狀態
     * 當訂單進入下一個階段時更新狀態
     * 例如：客戶付款後，從「待付款」變更為「已付款」
     * @param orderId 訂單 ID
     * @param status 新的訂單狀態
     */
    void updateOrderStatus(Long orderId, String status);
    /**
     * 取消訂單
     * 客戶或管理員取消訂單
     * 只有特定狀態的訂單可以取消（例如：不能取消已送達的訂單）
     * @param orderId 訂單 ID
     */
    void cancelOrder(Long orderId);
}