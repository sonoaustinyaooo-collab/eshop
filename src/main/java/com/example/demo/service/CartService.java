package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;

/**
 * 購物車 Service 介面
 * 定義購物車相關的業務邏輯方法
 */
public interface CartService {
    
    /**
     * 取得客戶的購物車
     * 如果不存在則自動建立
     */
    Cart getOrCreateCart(Customer customer);
    
    /**
     * 新增產品到購物車
     */
    void addProductToCart(Long customerId, Long productId, Integer quantity);
    
    /**
     * 更新購物車項目數量
     */
    void updateCartItemQuantity(Long cartItemId, Integer quantity);
    
    /**
     * 從購物車移除項目
     */
    void removeCartItem(Long cartItemId);
    
    /**
     * 清空購物車
     */
    void clearCart(Long customerId);
    
    /**
     * 取得購物車
     */
    Cart getCartByCustomerId(Long customerId);
}