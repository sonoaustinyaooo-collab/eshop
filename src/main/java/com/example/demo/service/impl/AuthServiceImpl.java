package com.example.demo.service.impl;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.Customer;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private CustomerDAO customerDAO;
    
    @Override
    public User adminLogin(String username, String password) {
        User user = userDAO.findByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        
        return null;
    }
    
    @Override
    public Customer customerLogin(String username, String password) {
        System.out.println("=== 開始顧客登入驗證 ===");
        System.out.println("輸入的帳號：" + username);
        
        // 去除前後空白
        username = username.trim();
        password = password.trim();
        
        Customer customer = customerDAO.findByUsername(username);
        
        if (customer == null) {
            System.out.println("❌ 找不到使用者：" + username);
            return null;
        }
        
        System.out.println("✓ 找到使用者：" + customer.getCustName());
        
        if (customer.getCustPassword().equals(password)) {
            System.out.println("✓ 密碼驗證成功");
            return customer;
        }
        
        System.out.println("❌ 密碼驗證失敗");
        return null;
    }
    
    @Override
    public Customer registerCustomer(Customer customer) {
        System.out.println("=== 開始註冊顧客 ===");
        
        // ===== 1. 驗證必填欄位 =====
        if (customer.getCustUsername() == null || customer.getCustUsername().trim().isEmpty()) {
            throw new RuntimeException("使用者名稱不能為空");
        }
        
        if (customer.getCustPassword() == null || customer.getCustPassword().trim().isEmpty()) {
            throw new RuntimeException("密碼不能為空");
        }
        
        if (customer.getCustName() == null || customer.getCustName().trim().isEmpty()) {
            throw new RuntimeException("真實姓名不能為空");
        }
        
        if (customer.getCustEmail() == null || customer.getCustEmail().trim().isEmpty()) {
            throw new RuntimeException("Email 不能為空");
        }
        
        // ===== 2. 去除所有欄位的前後空白 =====
        customer.setCustUsername(customer.getCustUsername().trim());
        customer.setCustPassword(customer.getCustPassword().trim());
        customer.setCustName(customer.getCustName().trim());
        customer.setCustEmail(customer.getCustEmail().trim());
        
        if (customer.getCustPhone() != null) {
            customer.setCustPhone(customer.getCustPhone().trim());
        }
        
        if (customer.getCustAddress() != null) {
            customer.setCustAddress(customer.getCustAddress().trim());
        }
        
        // ===== 3. 驗證使用者名稱格式 =====
        // 只允許英文字母、數字、底線，長度 4-20 字元
        if (!customer.getCustUsername().matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new RuntimeException("使用者名稱格式錯誤：只能包含英文、數字、底線，長度 4-20 字元");
        }
        
        // ===== 4. 驗證密碼長度 =====
        if (customer.getCustPassword().length() < 6) {
            throw new RuntimeException("密碼長度至少 6 個字元");
        }
        
        if (customer.getCustPassword().length() > 50) {
            throw new RuntimeException("密碼長度不能超過 50 個字元");
        }
        
        // ===== 5. 驗證 Email 格式 =====
        if (!customer.getCustEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Email 格式錯誤");
        }
        
        // ===== 6. 驗證電話格式（如果有填寫） =====
        if (customer.getCustPhone() != null && !customer.getCustPhone().isEmpty()) {
            // 移除所有非數字字元
            String phoneDigits = customer.getCustPhone().replaceAll("[^0-9]", "");
            
            // 檢查是否為 10 位數字
            if (phoneDigits.length() != 10) {
                throw new RuntimeException("電話號碼格式錯誤：請輸入 10 位數字");
            }
            
            // 儲存純數字格式
            customer.setCustPhone(phoneDigits);
        }
        
        // ===== 7. 檢查使用者名稱是否已存在 =====
        if (isUsernameExist(customer.getCustUsername())) {
            throw new RuntimeException("使用者名稱已存在");
        }
        
        // ===== 8. 檢查 Email 是否已被使用 =====
        Customer existingCustomer = customerDAO.findByEmail(customer.getCustEmail());
        if (existingCustomer != null) {
            throw new RuntimeException("Email 已被使用");
        }
        
        // ===== 9. 儲存顧客資料 =====
        System.out.println("使用者名稱：[" + customer.getCustUsername() + "]");
        System.out.println("密碼：[" + customer.getCustPassword() + "]");
        System.out.println("姓名：[" + customer.getCustName() + "]");
        System.out.println("Email：[" + customer.getCustEmail() + "]");
        
        customerDAO.save(customer);
        System.out.println("✓ 顧客註冊成功");
        
        return customer;
    }
    
    @Override
    public boolean isUsernameExist(String username) {
        // 去除空白後再檢查
        username = username.trim();
        
        // 檢查管理員
        User user = userDAO.findByUsername(username);
        if (user != null) {
            return true;
        }
        
        // 檢查顧客
        Customer customer = customerDAO.findByUsername(username);
        return customer != null;
    }
}