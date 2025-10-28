package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.User;

/**
 * 認證服務介面
 * 處理使用者和顧客的登入認證
 */
public interface AuthService {
    
    /**
     * 管理員登入
     * @param username 使用者名稱
     * @param password 密碼
     * @return 登入成功返回 User 物件，失敗返回 null
     */
    User adminLogin(String username, String password);
    
    /**
     * 顧客登入
     * @param username 使用者名稱
     * @param password 密碼
     * @return 登入成功返回 Customer 物件，失敗返回 null
     */
    Customer customerLogin(String username, String password);
    
    /**
     * 顧客註冊
     * @param customer 顧客資料
     * @return 註冊成功的顧客物件
     */
    Customer registerCustomer(Customer customer);
    
    /**
     * 檢查使用者名稱是否已存在
     * @param username 使用者名稱
     * @return true 表示已存在
     */
    boolean isUsernameExist(String username);
}