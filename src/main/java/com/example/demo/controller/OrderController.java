package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.util.SessionHelper;  // 引入 SessionHelper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

/**
 * 訂單 Controller（更新版）
 * 使用 SessionHelper 管理登入狀態
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;

    /**
     * 顯示結帳頁面
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        try {
            // 使用 SessionHelper 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 檢查是否登入
            if (customerId == null) {
                model.addAttribute("error", "請先登入");
                return "redirect:/login";
            }
            
            // 取得購物車資料（用於顯示訂單摘要）
            var cart = cartService.getCartByCustomerId(customerId);
            model.addAttribute("cart", cart);
            
            // 可以預先載入顧客資料，方便自動填入表單
            // Customer customer = customerService.getCustomerById(customerId);
            // model.addAttribute("customer", customer);
            
            return "checkout";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 處理建立訂單
     */
    @PostMapping("/create")
    public String createOrder(
            @RequestParam String recipientName,
            @RequestParam String recipientPhone,
            @RequestParam String shippingAddress,
            @RequestParam(required = false) String orderNote,
            HttpSession session) {
        
        try {
            // 使用 SessionHelper 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 檢查是否登入
            if (customerId == null) {
                return "redirect:/login?error=請先登入";
            }
            
            // 建立訂單
            Order order = orderService.createOrderFromCart(
                customerId, 
                recipientName, 
                recipientPhone, 
                shippingAddress, 
                orderNote
            );
            
            // 導向訂單詳細頁面
            return "redirect:/orders/" + order.getOrderId();
            
        } catch (RuntimeException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }

    /**
     * 顯示訂單詳細頁面
     */
    @GetMapping("/{orderId}")
    public String viewOrderDetail(@PathVariable Long orderId,
                                 HttpSession session,
                                 Model model) {
        try {
            // 取得訂單
            Order order = orderService.getOrderById(orderId);
            
            // ===== 權限檢查 =====
            // 檢查訂單是否屬於當
            Long customerId = SessionHelper.getCurrentCustomerId(session);
         // 如果是顧客登入，檢查訂單是否屬於該顧客
            if (SessionHelper.isCustomer(session)) {
                // order.getCustomer().getCustNum() 取得訂單所屬的顧客 ID
                // .equals() 比較是否相同
                if (!order.getCustomer().getCustNum().equals(customerId)) {
                    // 訂單不屬於當前顧客，無權查看
                    return "redirect:/orders?error=無權限查看此訂單";
                }
            }
            // 如果是管理員，可以查看所有訂單
            
            // 將訂單資料加入 Model
            model.addAttribute("order", order);
            
            return "order-detail";
            
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 顯示我的訂單列表
     */
    @GetMapping
    public String listMyOrders(HttpSession session, Model model) {
        try {
            // 使用 SessionHelper 取得顧客 ID
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 檢查是否登入
            if (customerId == null) {
                model.addAttribute("notLoggedIn", true);
                return "orders";
            }
            
            // 取得該顧客的所有訂單
            var orders = orderService.getOrdersByCustomerId(customerId);
            
            model.addAttribute("orders", orders);
            model.addAttribute("notLoggedIn", false);
            
            return "orders";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 取消訂單
     */
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,
                             HttpSession session) {
        try {
            // 取得訂單
            Order order = orderService.getOrderById(orderId);
            
            // ===== 權限檢查 =====
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            
            // 如果是顧客，檢查訂單是否屬於該顧客
            if (SessionHelper.isCustomer(session)) {
                if (!order.getCustomer().getCustNum().equals(customerId)) {
                    return "redirect:/orders?error=無權限取消此訂單";
                }
            }
            // 管理員可以取消任何訂單
            
            // 執行取消訂單
            orderService.cancelOrder(orderId);
            
            return "redirect:/orders";
            
        } catch (RuntimeException e) {
            return "redirect:/orders?error=" + e.getMessage();
        }
    }}