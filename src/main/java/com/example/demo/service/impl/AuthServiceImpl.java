package com.example.demo.service.impl;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.Customer;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認證服務實作類別
 * 處理登入和註冊的業務邏輯
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private CustomerDAO customerDAO;
    
    /**
     * 管理員登入
     * 驗證使用者名稱和密碼
     */
    @Override
    public User adminLogin(String username, String password) {
        // 根據使用者名稱查詢
        User user = userDAO.findByUsername(username);
        
        // 檢查使用者是否存在
        if (user == null) {
            return null;  // 使用者不存在
        }
        
        // 驗證密碼
        // TODO: 實際應該使用加密比對（例如 BCrypt）
        if (user.getPassword().equals(password)) {
            return user;  // 登入成功
        }
        
        return null;  // 密碼錯誤
    }
    
    /**
     * 顧客登入
     * 驗證顧客使用者名稱和密碼
     */
    @Override
    public Customer customerLogin(String username, String password) {
        // 根據使用者名稱查詢顧客
        Customer customer = customerDAO.findByUsername(username);
        
        // 檢查顧客是否存在
        if (customer == null) {
            return null;  // 顧客不存在
        }
        
        // 驗證密碼
        // TODO: 實際應該使用加密比對
        if (customer.getCustPassword().equals(password)) {
            return customer;  // 登入成功
        }
        
        return null;  // 密碼錯誤
    }
    
    /**
     * 顧客註冊
     * 建立新的顧客帳號
     */
    @Override
    public Customer registerCustomer(Customer customer) {
        // 檢查使用者名稱是否已存在
        if (isUsernameExist(customer.getCustUsername())) {
            throw new RuntimeException("使用者名稱已存在");
        }
        
        // 檢查 Email 是否已存在
        Customer existingCustomer = customerDAO.findByEmail(customer.getCustEmail());
        if (existingCustomer != null) {
            throw new RuntimeException("Email 已被使用");
        }
        
        // TODO: 密碼應該加密後儲存
        // String encryptedPassword = BCrypt.hashpw(customer.getCustPassword(), BCrypt.gensalt());
        // customer.setCustPassword(encryptedPassword);
        
        // 儲存顧客資料
        customerDAO.save(customer);
        
        return customer;
    }
    
    /**
     * 檢查使用者名稱是否已存在
     * 同時檢查管理員和顧客的使用者名稱
     */
    @Override
    public boolean isUsernameExist(String username) {
        // 檢查管理員
        User user = userDAO.findByUsername(username);
        if (user != null) {
            return true;
        }
        
        // 檢查顧客
        Customer customer = customerDAO.findByUsername(username);
        if (customer != null) {
            return true;
        }
        
        return false;
    }
}