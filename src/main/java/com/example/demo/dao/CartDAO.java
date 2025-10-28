package com.example.demo.dao;

import com.example.demo.model.Cart;
import com.example.demo.model.Customer;

/**
 * 購物車 DAO 介面
 * 定義購物車相關的資料存取方法
 */
public interface CartDAO {
    
    /**
     * 根據 ID 查詢購物車
     */
    Cart findById(Long id);
    
    /**
     * 根據客戶查詢購物車
     * 每個客戶只有一個購物車
     */
    Cart findByCustomer(Customer customer);
    
    /**
     * 儲存或更新購物車
     */
    void save(Cart cart);
    
    /**
     * 刪除購物車
     */
    void delete(Long id);
}