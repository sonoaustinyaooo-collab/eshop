package com.example.demo.dao.impl;  // 定義套件（impl = implementation 實作）

// 引入需要的類別
import com.example.demo.dao.OrderDAO;  // OrderDAO 介面
import com.example.demo.model.Customer;  // 客戶實體
import com.example.demo.model.Order;  // 訂單實體
import com.example.demo.model.OrderStatus;  // 訂單狀態枚舉
import org.hibernate.Session;  // Hibernate Session（類似 JDBC 的 Connection）
import org.hibernate.SessionFactory;  // Hibernate SessionFactory（產生 Session 的工廠）
import org.hibernate.query.Query;  // Hibernate Query 物件
import org.springframework.beans.factory.annotation.Autowired;  // Spring 自動注入註解
import org.springframework.stereotype.Repository;  // Spring Repository 註解
import java.util.List;  // List 介面

/**
 * 訂單 DAO 實作類別
 * 實作 OrderDAO 介面定義的所有方法
 * 使用 Hibernate Session 進行資料庫操作
 */
@Repository  // 標示這是一個 Repository（資料存取層）元件，Spring 會自動掃描並註冊
public class OrderDAOImpl implements OrderDAO {  // 實作 OrderDAO 介面
    
    // ========== 依賴注入 ==========
    
    @Autowired  // Spring 自動注入 SessionFactory 物件
    // SessionFactory 是 Hibernate 的核心物件，用於產生 Session
    private SessionFactory sessionFactory;
    
    /**
     * 取得當前 Hibernate Session
     * Session 是 Hibernate 用來執行資料庫操作的物件
     * 類似於 JDBC 的 Connection
     * @return 當前的 Hibernate Session
     */
    private Session getCurrentSession() {
        // sessionFactory.getCurrentSession() 會取得與當前事務綁定的 Session
        // 這個 Session 會在事務結束時自動關閉，不需手動關閉
        return sessionFactory.getCurrentSession();
    }
    
    // ========== 實作 DAO 方法 ==========
    
    /**
     * 根據 ID 查詢訂單
     * 實作 OrderDAO 介面的 findById 方法
     * @param id 訂單 ID
     * @return 訂單物件，找不到則回傳 null
     */
    @Override  // 標示此方法是覆寫介面的方法
    public Order findById(Long id) {
        // getCurrentSession() 取得 Session
        // .get(Order.class, id) 根據主鍵查詢訂單
        // 參數1：要查詢的實體類別
        // 參數2：主鍵值
        // 如果找不到會回傳 null
        return getCurrentSession().get(Order.class, id);
    }
    
    /**
     * 根據訂單編號查詢
     * 實作 OrderDAO 介面的 findByOrderNumber 方法
     * @param orderNumber 訂單編號
     * @return 訂單物件，找不到則回傳 null
     */
    @Override
    public Order findByOrderNumber(String orderNumber) {
        // 建立 HQL 查詢（HQL = Hibernate Query Language，類似 SQL 但操作物件）
        // "FROM Order" 表示從 Order 實體查詢（不是從 orders 資料表）
        // "WHERE orderNumber = :orderNumber" 是查詢條件
        // :orderNumber 是命名參數（named parameter），稍後會設定值
        Query<Order> query = getCurrentSession()
            .createQuery("FROM Order WHERE orderNumber = :orderNumber", Order.class)
            // .setParameter() 設定命名參數的值
            // 第一個參數：參數名稱（對應 HQL 中的 :orderNumber）
            // 第二個參數：參數值（要查詢的訂單編號）
            .setParameter("orderNumber", orderNumber);
        
        // .uniqueResult() 回傳唯一的查詢結果
        // 如果找不到回傳 null
        // 如果找到多筆會拋出例外（因為訂單編號是唯一的）
        return query.uniqueResult();
    }
    
    /**
     * 查詢所有訂單
     * 實作 OrderDAO 介面的 findAll 方法
     * @return 所有訂單的 List
     */
    @Override
    public List<Order> findAll() {
        // HQL 查詢：FROM Order ORDER BY createdDate DESC
        // FROM Order：查詢所有訂單
        // ORDER BY createdDate DESC：按建立時間降序排列（最新的在前）
        // DESC = descending（降序），ASC = ascending（升序）
        return getCurrentSession()
            .createQuery("FROM Order ORDER BY createdDate DESC", Order.class)
            // .list() 回傳查詢結果的 List 集合
            // 如果沒有資料會回傳空的 List（不是 null）
            .list();
    }
    
    /**
     * 根據客戶查詢訂單
     * 實作 OrderDAO 介面的 findByCustomer 方法
     * @param customer 客戶物件
     * @return 該客戶的所有訂單 List
     */
    @Override
    public List<Order> findByCustomer(Customer customer) {
        // 建立 HQL 查詢
        // FROM Order：從訂單實體查詢
        // WHERE customer = :customer：條件是客戶等於指定客戶
        // ORDER BY createdDate DESC：按建立時間降序（最新的在前）
        Query<Order> query = getCurrentSession()
            .createQuery("FROM Order WHERE customer = :customer ORDER BY createdDate DESC", Order.class)
            // 設定參數：customer 參數的值為傳入的 customer 物件
            // Hibernate 會自動根據客戶的 ID 查詢
            .setParameter("customer", customer);
        
        // 回傳查詢結果的 List
        return query.list();
    }
    
    /**
     * 根據訂單狀態查詢
     * 實作 OrderDAO 介面的 findByStatus 方法
     * @param status 訂單狀態枚舉
     * @return 符合該狀態的所有訂單 List
     */
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        // 建立 HQL 查詢
        // FROM Order：從訂單實體查詢
        // WHERE orderStatus = :status：條件是訂單狀態等於指定狀態
        // ORDER BY createdDate DESC：按建立時間降序
        Query<Order> query = getCurrentSession()
            .createQuery("FROM Order WHERE orderStatus = :status ORDER BY createdDate DESC", Order.class)
            // 設定參數：status 參數的值為傳入的枚舉值
            // 例如：OrderStatus.PENDING_PAYMENT
            .setParameter("status", status);
        
        // 回傳查詢結果的 List
        return query.list();
    }
    
    /**
     * 儲存或更新訂單
     * 實作 OrderDAO 介面的 save 方法
     * @param order 要儲存或更新的訂單物件
     */
    @Override
    public void save(Order order) {
        // .saveOrUpdate() 是 Hibernate 的方法
        // 如果 order 的 ID 是 null（新訂單），則執行 INSERT（新增）
        // 如果 order 的 ID 不是 null（已存在的訂單），則執行 UPDATE（更新）
        // Hibernate 會自動判斷要執行哪種操作
        getCurrentSession().saveOrUpdate(order);
    }
    
    /**
     * 刪除訂單
     * 實作 OrderDAO 介面的 delete 方法
     * @param id 訂單 ID
     */
    @Override
    public void delete(Long id) {
        // 先根據 ID 查詢訂單
        Order order = getCurrentSession().get(Order.class, id);
        
        // 檢查訂單是否存在
        if (order != null) {  // 如果訂單存在
            // .delete() 刪除訂單
            // 由於 Order 實體設定了 cascade = CascadeType.ALL
            // 相關的訂單項目（OrderItem）也會被自動刪除
            getCurrentSession().delete(order);
        }
        // 如果訂單不存在（order == null），不做任何事
    }
}