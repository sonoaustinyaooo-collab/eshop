package com.example.demo.dao.impl;

// 引入需要的類別
import com.example.demo.dao.CustomerDAO;  // CustomerDAO 介面
import com.example.demo.model.Customer;  // Customer 實體類別
import org.hibernate.Session;  // Hibernate Session
import org.hibernate.SessionFactory;  // SessionFactory
import org.hibernate.query.Query;  // Hibernate Query
import org.springframework.beans.factory.annotation.Autowired;  // Spring 自動注入
import org.springframework.stereotype.Repository;  // Repository 註解
import java.util.List;  // Java List 集合

/**
 * 顧客 DAO 實作類別
 * 
 * 這個類別實作 CustomerDAO 介面，提供顧客的資料庫操作功能
 * 包含：查詢、新增、更新、刪除顧客資料
 * 
 * @Repository 表示這是一個資料存取層元件
 * Spring 會自動掃描並註冊此類別為 Bean
 */
@Repository
public class CustomerDAOImpl implements CustomerDAO {
    
    // ========== 依賴注入 ==========
    
    /**
     * Hibernate SessionFactory
     * 用於建立和管理 Hibernate Session
     * @Autowired 表示由 Spring 自動注入
     */
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * 取得當前的 Hibernate Session
     * 
     * getCurrentSession() 會返回與當前執行緒綁定的 Session
     * 這個 Session 會在交易結束時自動關閉
     * 
     * @return 當前的 Hibernate Session
     */
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    // ========== CRUD 操作 ==========
    
    /**
     * 查詢所有顧客
     * 
     * 使用 HQL (Hibernate Query Language) 查詢所有顧客資料
     * 
     * @return 所有顧客的 List 集合
     */
    @Override
    public List<Customer> findAll() {
        // createQuery() 建立 HQL 查詢
        // "FROM Customer" 表示查詢所有 Customer 實體
        // Customer.class 指定返回類型
        // .list() 執行查詢並返回結果列表
        return getCurrentSession()
            .createQuery("FROM Customer", Customer.class)
            .list();
    }
    
    /**
     * 根據 ID 查詢顧客
     * 
     * 使用 Hibernate 的 get() 方法根據主鍵查詢資料
     * 
     * @param id 顧客 ID（主鍵）
     * @return Customer 物件，如果不存在則返回 null
     */
    @Override
    public Customer findById(Long id) {
        // get() 方法根據主鍵查詢
        // 參數1：實體類別（Customer.class）
        // 參數2：主鍵值（id）
        // 如果找不到，返回 null
        return getCurrentSession().get(Customer.class, id);
    }
    
    /**
     * 儲存或更新顧客資料
     * 
     * 使用 Hibernate 的 saveOrUpdate() 方法
     * - 如果顧客 ID 為 null 或不存在 → 執行 INSERT（新增）
     * - 如果顧客 ID 已存在 → 執行 UPDATE（更新）
     * 
     * @param customer 要儲存或更新的顧客物件
     */
    @Override
    public void save(Customer customer) {
        // saveOrUpdate() 自動判斷新增或更新
        getCurrentSession().saveOrUpdate(customer);
    }
    
    /**
     * 根據 ID 刪除顧客
     * 
     * 執行流程：
     * 1. 先根據 ID 查詢顧客是否存在
     * 2. 如果存在，執行刪除操作
     * 3. 如果不存在，不執行任何操作
     * 
     * @param id 要刪除的顧客 ID
     */
    @Override
    public void delete(Long id) {
        // 步驟1：根據 ID 查詢顧客
        Customer customer = getCurrentSession().get(Customer.class, id);
        
        // 步驟2：檢查顧客是否存在
        if (customer != null) {
            // 步驟3：如果存在，執行刪除
            getCurrentSession().delete(customer);
        }
    }
    
    // ========== 自訂查詢方法 ==========
    
    /**
     * 根據顧客姓名查詢
     * 
     * 這個方法用於搜尋功能，查詢所有符合指定姓名的顧客
     * 使用 = 運算符進行精確匹配
     * 
     * @param custName 顧客姓名
     * @return 符合條件的顧客 List（可能有多個同名顧客）
     */
    @Override
    public List<Customer> findByCustomerName(String custName) {
        // 建立 HQL 查詢
        // "WHERE custName = :name" 根據姓名篩選
        // :name 是參數佔位符
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custName = :name", Customer.class)
            .setParameter("name", custName);  // 設定參數值
        
        // 執行查詢並返回結果列表
        return query.list();
    }
    
    /**
     * 根據顧客 Email 查詢
     * 
     * Email 是唯一的，所以這個方法只會返回一個結果或 null
     * 
     * @param custEmail 顧客 Email
     * @return Customer 物件，如果不存在則返回 null
     */
    @Override
    public Customer findByCustomerEmail(String custEmail) {
        // 建立 HQL 查詢
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custEmail = :email", Customer.class)
            .setParameter("email", custEmail);  // 設定參數值
        
        // uniqueResult() 返回單一結果
        // 如果查詢結果有多筆，會拋出例外
        // 如果沒有結果，返回 null
        return query.uniqueResult();
    }
    
    /**
     * 根據使用者名稱查詢顧客
     * 
     * 這個方法用於登入功能
     * 使用者名稱是唯一的，所以只會返回一個結果或 null
     * 
     * @param username 使用者名稱
     * @return Customer 物件，如果不存在則返回 null
     */
    @Override
    public Customer findByUsername(String username) {
        // ===== 除錯訊息 =====
        System.out.println("查詢使用者：" + username);
        
        // 建立 HQL 查詢
        // "WHERE custUsername = :username" 根據使用者名稱篩選
        // 注意：這裡使用的是 Java 屬性名稱 custUsername
        // 不是資料庫欄位名稱 cust_username
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custUsername = :username", Customer.class)
            .setParameter("username", username);  // 設定參數值
        
        // 執行查詢
        Customer customer = query.uniqueResult();
        
        // ===== 除錯訊息 =====
        System.out.println("查詢結果：" + (customer != null ? "找到" : "未找到"));
        
        // 返回查詢結果
        return customer;
    }
    
    /**
     * 根據 Email 查詢顧客（用於註冊驗證）
     * 
     * 這個方法用於註冊時檢查 Email 是否已被使用
     * 與 findByCustomerEmail 功能相同，但命名更明確
     * 
     * @param email Email 地址
     * @return Customer 物件，如果不存在則返回 null
     */
    @Override
    public Customer findByEmail(String email) {
        // 建立 HQL 查詢
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custEmail = :email", Customer.class)
            .setParameter("email", email);
        
        // 返回單一結果
        return query.uniqueResult();
    }
}