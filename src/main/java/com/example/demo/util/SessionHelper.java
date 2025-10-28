package com.example.demo.util;

import com.example.demo.model.Customer;
import com.example.demo.model.User;
import javax.servlet.http.HttpSession;

/**
 * Session 輔助類別
 * 提供便捷的方法來管理使用者登入狀態
 * 集中管理 Session 相關的邏輯
 */
public class SessionHelper {
    
    // ========== Session 屬性名稱常數 ==========
    // 使用常數避免拼寫錯誤，方便維護
    
    /** 登入的管理員使用者物件 */
    private static final String LOGGED_IN_USER = "loggedInUser";
    
    /** 登入的顧客物件 */
    private static final String LOGGED_IN_CUSTOMER = "loggedInCustomer";
    
    /** 使用者類型（admin 或 customer） */
    private static final String USER_TYPE = "userType";
    
    /** 管理員 ID */
    private static final String USER_ID = "userId";
    
    /** 顧客 ID */
    private static final String CUSTOMER_ID = "customerId";
    
    // ========== 管理員相關方法 ==========
    
    /**
     * 設定管理員登入狀態
     * 將管理員資訊儲存到 Session
     * @param session HTTP Session 物件
     * @param user 管理員使用者物件
     */
    public static void setAdminSession(HttpSession session, User user) {
        // session.setAttribute() 設定 Session 屬性
        // 參數1：屬性名稱（key）
        // 參數2：屬性值（value）
        session.setAttribute(LOGGED_IN_USER, user);
        session.setAttribute(USER_TYPE, "admin");
        session.setAttribute(USER_ID, user.getId());
    }
    
    /**
     * 取得登入的管理員
     * 從 Session 取得管理員物件
     * @param session HTTP Session 物件
     * @return 管理員物件，如果未登入則返回 null
     */
    public static User getLoggedInAdmin(HttpSession session) {
        // session.getAttribute() 取得 Session 屬性
        // 返回值是 Object 類型，需要強制轉型
        return (User) session.getAttribute(LOGGED_IN_USER);
    }
    
    /**
     * 檢查是否為管理員
     * @param session HTTP Session 物件
     * @return true 表示是管理員，false 表示不是
     */
    public static boolean isAdmin(HttpSession session) {
        // 取得使用者類型
        String userType = (String) session.getAttribute(USER_TYPE);
        
        // 比較字串是否等於 "admin"
        // "admin".equals(userType) 而不是 userType.equals("admin")
        // 避免 userType 為 null 時拋出 NullPointerException
        return "admin".equals(userType);
    }
    
    // ========== 顧客相關方法 ==========
    
    /**
     * 設定顧客登入狀態
     * 將顧客資訊儲存到 Session
     * @param session HTTP Session 物件
     * @param customer 顧客物件
     */
    public static void setCustomerSession(HttpSession session, Customer customer) {
        session.setAttribute(LOGGED_IN_CUSTOMER, customer);
        session.setAttribute(USER_TYPE, "customer");
        session.setAttribute(CUSTOMER_ID, customer.getCustNum());
    }
    
    /**
     * 取得登入的顧客
     * 從 Session 取得顧客物件
     * @param session HTTP Session 物件
     * @return 顧客物件，如果未登入則返回 null
     */
    public static Customer getLoggedInCustomer(HttpSession session) {
        return (Customer) session.getAttribute(LOGGED_IN_CUSTOMER);
    }
    
    /**
     * 檢查是否為顧客
     * @param session HTTP Session 物件
     * @return true 表示是顧客，false 表示不是
     */
    public static boolean isCustomer(HttpSession session) {
        String userType = (String) session.getAttribute(USER_TYPE);
        return "customer".equals(userType);
    }
    
    /**
     * 取得當前登入的顧客 ID
     * 便捷方法，常用於購物車和訂單功能
     * @param session HTTP Session 物件
     * @return 顧客 ID，如果未登入則返回 null
     */
    public static Long getCurrentCustomerId(HttpSession session) {
        return (Long) session.getAttribute(CUSTOMER_ID);
    }
    
    // ========== 通用方法 ==========
    
    /**
     * 檢查是否已登入（管理員或顧客）
     * @param session HTTP Session 物件
     * @return true 表示已登入，false 表示未登入
     */
    public static boolean isLoggedIn(HttpSession session) {
        // 檢查 userType 是否存在
        // 如果存在表示已登入（可能是管理員或顧客）
        String userType = (String) session.getAttribute(USER_TYPE);
        return userType != null;
    }
    
    /**
     * 登出
     * 清除所有 Session 資料
     * @param session HTTP Session 物件
     */
    public static void logout(HttpSession session) {
        // session.invalidate() 使 Session 失效
        // 這會清除所有儲存在 Session 中的屬性
        session.invalidate();
    }
    
    /**
     * 取得使用者類型
     * @param session HTTP Session 物件
     * @return 使用者類型字串（"admin" 或 "customer"），未登入則返回 null
     */
    public static String getUserType(HttpSession session) {
        return (String) session.getAttribute(USER_TYPE);
    }
}