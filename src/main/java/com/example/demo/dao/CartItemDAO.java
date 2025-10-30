package com.example.demo.dao;

import com.example.demo.model.CartItem;
import java.util.List;
/**
 * 購物車項目 DAO 介面
 * 定義購物車項目相關的資料存取方法
 */
public interface CartItemDAO {

    
     // 根據 ID 查詢購物車項目
	List<CartItem> findAll(); 
    CartItem findById(Long id);
     //儲存或更新購物車項目
    void save(CartItem cartItem);
    //刪除購物車項目
     void delete(Long id);
     List<CartItem> findByCartId(Long cartId);
}