package com.example.demo.dao.impl;

// 引入需要的類別
import com.example.demo.dao.CartItemDAO;  // CartItemDAO 介面
import com.example.demo.model.CartItem;  // CartItem 實體類別
import org.hibernate.Session;  // Hibernate Session，用於資料庫操作
import org.hibernate.SessionFactory;  // SessionFactory，用於建立 Session
import org.hibernate.query.Query;  // Hibernate Query，用於執行 HQL 查詢
import org.springframework.beans.factory.annotation.Autowired;  // Spring 自動注入
import org.springframework.stereotype.Repository;  // Repository 註解
import java.util.List;  // Java List 集合

/**
 * 購物車項目 DAO 實作類別
 * 
 * 這個類別實作 CartItemDAO 介面，提供購物車項目的資料庫操作功能
 * 包含：查詢、新增、更新、刪除購物車項目
 * 
 * @Repository 表示這是一個資料存取層元件
 * Spring 會自動掃描並註冊此類別為 Bean
 */
@Repository
public class CartItemDAOImpl implements CartItemDAO {

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
     * 這個 Session 會在交易結束時自動關閉，不需要手動關閉
     * 
     * @return 當前的 Hibernate Session
     */
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    // ========== CRUD 操作 ==========

    /**
     * 根據 ID 查詢購物車項目
     * 
     * 使用 Hibernate 的 get() 方法根據主鍵查詢資料
     * - 如果找到資料，返回對應的 CartItem 物件
     * - 如果找不到資料，返回 null
     * 
     * @param id 購物車項目的 ID（主鍵）
     * @return CartItem 物件，如果不存在則返回 null
     */
    @Override
    public CartItem findById(Long id) {
        // getCurrentSession() 取得當前 Session
        // .get(CartItem.class, id) 根據 ID 查詢 CartItem
        // 參數1：實體類別（CartItem.class）
        // 參數2：主鍵值（id）
        return getCurrentSession().get(CartItem.class, id);
    }

    /**
     * 儲存或更新購物車項目
     * 
     * 使用 Hibernate 的 saveOrUpdate() 方法
     * - 如果購物車項目的 ID 為 null 或不存在，執行 INSERT（新增）
     * - 如果購物車項目的 ID 已存在，執行 UPDATE（更新）
     * 
     * 這個方法會自動判斷是新增還是更新，不需要額外判斷
     * 
     * @param cartItem 要儲存或更新的購物車項目物件
     */
    @Override
    public void save(CartItem cartItem) {
        // saveOrUpdate() 自動判斷新增或更新
        // 如果 cartItem.getCartItemId() 是 null → 新增
        // 如果 cartItem.getCartItemId() 有值 → 更新
        getCurrentSession().saveOrUpdate(cartItem);
    }

    /**
     * 根據 ID 刪除購物車項目
     * 
     * 執行流程：
     * 1. 先根據 ID 查詢購物車項目是否存在
     * 2. 如果存在，執行刪除操作
     * 3. 如果不存在，不執行任何操作（避免錯誤）
     * 
     * @param id 要刪除的購物車項目 ID
     */
    @Override
    public void delete(Long id) {
        // 步驟1：根據 ID 查詢購物車項目
        // get() 方法如果找不到會返回 null
        CartItem cartItem = getCurrentSession().get(CartItem.class, id);
        
        // 步驟2：檢查購物車項目是否存在
        if (cartItem != null) {
            // 步驟3：如果存在，執行刪除
            // delete() 方法會執行 SQL DELETE 語句
            getCurrentSession().delete(cartItem);
        }
        // 如果 cartItem 是 null，表示資料不存在，不執行刪除
    }

    // ========== 補充方法（建議加入） ==========
    
    /**
     * 查詢所有購物車項目
     * 
     * 使用 HQL (Hibernate Query Language) 查詢所有購物車項目
     * HQL 使用實體類別名稱而不是資料表名稱
     * 
     * @return 所有購物車項目的 List
     */
    @Override
    public List<CartItem> findAll() {
        // createQuery() 建立 HQL 查詢
        // "FROM CartItem" 是 HQL 語法，查詢所有 CartItem
        // 注意：這裡使用的是類別名稱 CartItem，不是資料表名稱 cart_items
        return getCurrentSession()
            .createQuery("FROM CartItem", CartItem.class)
            .list();  // .list() 執行查詢並返回結果集合
    }
    
    /**
     * 根據購物車 ID 查詢所有項目
     * 
     * 這個方法用於取得某個購物車中的所有商品項目
     * 例如：當使用者查看購物車時，需要顯示該購物車中的所有商品
     * 
     * @param cartId 購物車 ID
     * @return 該購物車中的所有項目
     */
    @Override
    public List<CartItem> findByCartId(Long cartId) {
        // 建立 HQL 查詢
        // "WHERE cart.cartId = :cartId" 表示根據購物車 ID 篩選
        // cart.cartId 中的 cart 是 CartItem 中的屬性名稱（關聯到 Cart）
        Query<CartItem> query = getCurrentSession()
            .createQuery("FROM CartItem WHERE cart.cartId = :cartId", CartItem.class)
            .setParameter("cartId", cartId);  // 設定參數值
        
        // 執行查詢並返回結果
        return query.list();
    }
}