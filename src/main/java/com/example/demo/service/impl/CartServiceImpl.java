package com.example.demo.service.impl;

import com.example.demo.dao.CartDAO;
import com.example.demo.dao.CartItemDAO;
import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

/**
 * 購物車 Service 實作類別
 * 處理購物車相關的業務邏輯
 */
@Service
@Transactional // 所有方法都在事務中執行
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartDAO cartDAO;
    
    @Autowired
    private CartItemDAO cartItemDAO;
    
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    /**
     * 取得或建立購物車
     * 如果客戶還沒有購物車，自動建立一個新的
     */
    @Override
    public Cart getOrCreateCart(Customer customer) {
        // 先嘗試查詢現有購物車
        Cart cart = cartDAO.findByCustomer(customer);
        
        // 如果沒有購物車，建立新的
        if (cart == null) {
            cart = new Cart(customer);
            cartDAO.save(cart);
        }
        
        return cart;
    }
    
    /**
     * 新增產品到購物車
     * 如果產品已存在，則增加數量
     */
    @Override
    public void addProductToCart(Long customerId, Long productId, Integer quantity) {
        // 驗證數量
        if (quantity <= 0) {
            throw new RuntimeException("數量必須大於 0");
        }
        
        // 查詢客戶
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + customerId);
        }
        
        // 查詢產品
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("找不到產品，ID: " + productId);
        }
        
        // 取得或建立購物車
        Cart cart = getOrCreateCart(customer);
        
        // 檢查產品是否已在購物車中
        CartItem existingItem = null;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getProdNum().equals(productId)) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            // 產品已存在，增加數量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // 產品不存在，新增到購物車
            CartItem newItem = new CartItem(product, quantity);
            cart.addCartItem(newItem);
        }
        
        // 更新購物車的修改時間
        cart.setUpdatedDate(new Date());
        cartDAO.save(cart);
    }
    
    /**
     * 更新購物車項目數量
     */
    @Override
    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("數量必須大於 0");
        }
        
        CartItem cartItem = cartItemDAO.findById(cartItemId);
        if (cartItem == null) {
            throw new RuntimeException("找不到購物車項目，ID: " + cartItemId);
        }
        
        // 更新數量
        cartItem.setQuantity(quantity);
        cartItemDAO.save(cartItem);
        
        // 更新購物車的修改時間
        Cart cart = cartItem.getCart();
        cart.setUpdatedDate(new Date());
        cartDAO.save(cart);
    }
    
    /**
     * 從購物車移除項目
     */
    @Override
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemDAO.findById(cartItemId);
        if (cartItem == null) {
            throw new RuntimeException("找不到購物車項目，ID: " + cartItemId);
        }
        
        Cart cart = cartItem.getCart();
        cart.removeCartItem(cartItem);
        
        // 更新購物車的修改時間
        cart.setUpdatedDate(new Date());
        cartDAO.save(cart);
    }
    
    /**
     * 清空購物車
     */
    @Override
    public void clearCart(Long customerId) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + customerId);
        }
        
        Cart cart = cartDAO.findByCustomer(customer);
        if (cart != null) {
            // 清空所有項目
            cart.getCartItems().clear();
            cart.setUpdatedDate(new Date());
            cartDAO.save(cart);
        }
    }
    
    /**
     * 根據客戶 ID 取得購物車
     */
    @Override
    public Cart getCartByCustomerId(Long customerId) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + customerId);
        }
        
        return cartDAO.findByCustomer(customer);
    }
}