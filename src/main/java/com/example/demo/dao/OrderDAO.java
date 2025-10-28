package com.example.demo.dao;  // 定義套件

// 引入需要的類別
import com.example.demo.model.Customer;  // 客戶實體類別
import com.example.demo.model.Order;  // 訂單實體類別
import com.example.demo.model.OrderStatus;  // 訂單狀態枚舉
import java.util.List;  // List 介面

/**
 * 訂單 DAO 介面
 * DAO = Data Access Object（資料存取物件）
 * 定義訂單相關的資料存取方法
 * 介面只定義方法簽名，不包含實作
 */
public interface OrderDAO {  // 定義 OrderDAO 介面
    
    /**
     * 根據 ID 查詢訂單
     * 這是最基本的查詢方法，根據主鍵查詢單一訂單
     * @param id 訂單 ID（主鍵）
     * @return 訂單物件，如果找不到則回傳 null
     */
    Order findById(Long id);
    
    /**
     * 根據訂單編號查詢
     * 訂單編號是對外顯示的編號（例如：ORD20250101001）
     * 客戶通常使用訂單編號查詢訂單
     * @param orderNumber 訂單編號字串
     * @return 訂單物件，如果找不到則回傳 null
     */
    Order findByOrderNumber(String orderNumber);
    
    /**
     * 查詢所有訂單
     * 管理員查看所有訂單時使用
     * @return 所有訂單的 List 集合
     */
    List<Order> findAll();
    
    /**
     * 根據客戶查詢訂單
     * 查詢特定客戶的所有訂單（例如：查看「我的訂單」）
     * @param customer 客戶物件
     * @return 該客戶的所有訂單 List 集合
     */
    List<Order> findByCustomer(Customer customer);
    
    /**
     * 根據訂單狀態查詢
     * 查詢特定狀態的所有訂單（例如：查看所有「待付款」的訂單）
     * @param status 訂單狀態枚舉
     * @return 符合該狀態的所有訂單 List 集合
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * 儲存或更新訂單
     * 如果訂單是新的（ID 為 null），則新增到資料庫
     * 如果訂單已存在（ID 不為 null），則更新資料庫中的記錄
     * @param order 要儲存或更新的訂單物件
     */
    void save(Order order);
    
    /**
     * 刪除訂單
     * 根據訂單 ID 從資料庫刪除訂單
     * 由於設定了 cascade，相關的訂單項目也會被刪除
     * @param id 訂單 ID
     */
    void delete(Long id);
}