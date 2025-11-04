package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.service.CartService;
import com.example.demo.util.SessionHelper;  // 引入 SessionHelper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpSession;  // 引入 HttpSession

/**
 * 購物車 Controller
 * 使用 SessionHelper 管理登入狀態
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 顯示購物車頁面
     * 使用 SessionHelper 取得當前登入的顧客 ID
     */
    @GetMapping
    @Transactional
    public String viewCart(HttpSession session, Model model) {
        try {
            // ===== 使用 SessionHelper 取得顧客 ID =====
            // SessionHelper.getCurrentCustomerId() 從 Session 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 檢查是否登入
            if (customerId == null) {
                // 未登入，設定標記
                model.addAttribute("cart", null);
                model.addAttribute("notLoggedIn", true);
                return "cart";
            }
            
            // 已登入，取得購物車
            Cart cart = cartService.getCartByCustomerId(customerId);
            
            if (cart != null && cart.getCartItems() != null) {
                cart.getCartItems().size();
            }
            model.addAttribute("cart", cart);
            model.addAttribute("notLoggedIn", false);
            return "cart";
            
        } catch (Exception e) {
        	e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cart", null);
            return "cart";
        }
    }

    /**
     * 新增產品到購物車
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, 
                           @RequestParam Integer quantity,
                           HttpSession session, // 加入 HttpSession 參數
                           Model model) {  
    	                   // Model 參數
    	
    	System.out.println("=== 加入購物車 ===");
        System.out.println("產品 ID: " + productId);
        System.out.println("數量: " + quantity);
        
        try {
            // 使用 SessionHelper 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            System.out.println("顧客 ID: " + customerId);
            
            // 檢查是否登入
            if (customerId == null) {
                // 未登入，導向登入頁面
            	System.out.println("❌ 未登入，導向登入頁面");
            	// 導向登入頁面
                return "redirect:/cart";
            }
            
            System.out.println("✓ 開始加入購物車");
            
            // 已登入，執行加入購物車
            cartService.addProductToCart(customerId, productId, quantity);
            
            System.out.println("✓ 加入購物車成功");
            
            // 導向購物車頁面
            return "redirect:/cart";
            
        } catch (RuntimeException e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    /**
     * 更新購物車項目數量
     */
    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(@PathVariable Long cartItemId, 
                                @RequestParam Integer quantity,
                                HttpSession session) {
        try {
            // 檢查是否登入
            if (!SessionHelper.isCustomer(session)) {
                return "redirect:/login?error=請先登入";
            }
            
            cartService.updateCartItemQuantity(cartItemId, quantity);
            return "redirect:/cart";
            
        } catch (RuntimeException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }

    /**
     * 從購物車移除項目
     */
    @GetMapping("/remove/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId,
                                HttpSession session) {
        try {
            // 檢查是否登入
            if (!SessionHelper.isCustomer(session)) {
                return "redirect:/login?error=請先登入";
            }
            
            cartService.removeCartItem(cartItemId);
            return "redirect:/cart";
            
        } catch (RuntimeException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }

    /**
     * 清空購物車
     */
    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        try {
            // 使用 SessionHelper 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 檢查是否登入
            if (customerId == null) {
                return "redirect:/login?error=請先登入";
            }
            
            cartService.clearCart(customerId);
            return "redirect:/cart";
            
        } catch (Exception e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
}