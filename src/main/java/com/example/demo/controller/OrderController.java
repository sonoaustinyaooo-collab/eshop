package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.util.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 訂單 Controller
 * 處理訂單相關的 HTTP 請求
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
     * URL: GET /orders/checkout
     */
    @GetMapping("/checkout")
    @Transactional(readOnly = true)
    public String showCheckoutPage(HttpSession session, Model model) {
        System.out.println("========== 顯示結帳頁面 ==========");
        
        try {
            // 檢查是否登入
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            if (customerId == null) {
                System.out.println("❌ 未登入，導向登入頁面");
                try {
                    String error = URLEncoder.encode("請先登入", "UTF-8");
                    return "redirect:/login?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/login";
                }
            }
            
            // 取得購物車
            Cart cart = cartService.getCartByCustomerId(customerId);
            
            // 檢查購物車是否為空
            if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
                System.out.println("❌ 購物車是空的");
                try {
                    String error = URLEncoder.encode("購物車是空的，無法結帳", "UTF-8");
                    return "redirect:/cart?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/cart";
                }
            }
            
            // 取得顧客資料（用於預填收件人資訊）
            Customer customer = SessionHelper.getLoggedInCustomer(session);
            
            // 傳遞資料到前端
            model.addAttribute("cart", cart);
            model.addAttribute("customer", customer);
            
            System.out.println("✓ 購物車項目數：" + cart.getCartItems().size());
            System.out.println("✓ 總金額：" + cart.getTotalAmount());
            
            return "checkout";
            
        } catch (Exception e) {
            System.out.println("❌ 顯示結帳頁面時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/cart?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/cart";
            }
        }
    }

    /**
     * 確認訂單（建立訂單）
     * URL: POST /orders/create
     */
    @PostMapping("/create")
    public String createOrder(
            @RequestParam String recipientName,
            @RequestParam String recipientPhone,
            @RequestParam String shippingAddress,
            @RequestParam(required = false) String orderNote,
            HttpSession session,
            Model model) {
        
        System.out.println("========== 建立訂單 ==========");
        System.out.println("收件人姓名：" + recipientName);
        System.out.println("收件人電話：" + recipientPhone);
        System.out.println("收件地址：" + shippingAddress);
        System.out.println("訂單備註：" + orderNote);
        
        try {
            // 檢查是否登入
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            if (customerId == null) {
                System.out.println("❌ 未登入");
                try {
                    String error = URLEncoder.encode("請先登入", "UTF-8");
                    return "redirect:/login?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/login";
                }
            }
            
            // 呼叫 Service 建立訂單
            Order order = orderService.createOrderFromCart(
                customerId,
                recipientName,
                recipientPhone,
                shippingAddress,
                orderNote
            );
            
            System.out.println("✓ 訂單建立成功");
            System.out.println("✓ 訂單編號：" + order.getOrderNumber());
            System.out.println("✓ 訂單 ID：" + order.getOrderId());
            System.out.println("====================================");
            
            // 導向訂單明細頁面
            try {
                String message = URLEncoder.encode("訂單建立成功", "UTF-8");
                return "redirect:/orders/" + order.getOrderId() + "?message=" + message;
            } catch (UnsupportedEncodingException e) {
                return "redirect:/orders/" + order.getOrderId();
            }
            
        } catch (RuntimeException e) {
            System.out.println("❌ 建立訂單失敗：" + e.getMessage());
            e.printStackTrace();
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/orders/checkout?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/orders/checkout";
            	}
            }
    }

    /**
     * 顯示訂單明細
     * URL: GET /orders/{orderId}
     */
    @GetMapping("/{orderId}")
    @Transactional(readOnly = true)
    public String showOrderDetail(@PathVariable Long orderId, 
                                  HttpSession session, 
                                  Model model) {
        System.out.println("========== 顯示訂單明細 ==========");
        System.out.println("訂單 ID：" + orderId);
        
        try {
            // 檢查是否登入
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            if (customerId == null) {
                System.out.println("❌ 未登入");
                try {
                    String error = URLEncoder.encode("請先登入", "UTF-8");
                    return "redirect:/login?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/login";
                }
            }
            
            // 取得訂單
            Order order = orderService.getOrderById(orderId);
            
         // 檢查訂單是否存在
            if (order == null) {
                System.out.println("❌ 訂單不存在");
                try {
                    String error = URLEncoder.encode("訂單不存在", "UTF-8");
                    return "redirect:/orders/my-orders?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/orders/my-orders";
                }
            }
            
            // 檢查訂單是否屬於當前顧客
            if (!order.getCustomer().getCustNum().equals(customerId)) {
                System.out.println("❌ 訂單不屬於當前顧客");
                try {
                    String error = URLEncoder.encode("無權查看此訂單", "UTF-8");
                    return "redirect:/orders/my-orders?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/orders/my-orders";
                }
            }
            
            // 傳遞訂單資料到前端
            model.addAttribute("order", order);
            
            System.out.println("✓ 訂單編號：" + order.getOrderNumber());
            System.out.println("✓ 訂單狀態：" + order.getOrderStatus().getDisplayName());
            System.out.println("✓ 訂單項目數：" + order.getOrderItems().size());
            
            return "order-detail";
            
        } catch (RuntimeException e) {
            System.out.println("❌ 查詢訂單失敗：" + e.getMessage());
            e.printStackTrace();
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/orders/my-orders?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/orders/my-orders";
            }
        }
    }

    /**
     * 顯示我的訂單列表
     * URL: GET /orders/my-orders
     */
    @GetMapping("/my-orders")
    @Transactional(readOnly = true)
    public String myOrders(HttpSession session, Model model) {
        System.out.println("========== 顯示我的訂單列表 ==========");
        
        try {
            // 檢查是否登入
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            if (customerId == null) {
                System.out.println("❌ 未登入");
                try {
                    String error = URLEncoder.encode("請先登入", "UTF-8");
                    return "redirect:/login?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/login";
                }
            }
            
            // 取得顧客的所有訂單
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            
            // 傳遞訂單列表到前端
            model.addAttribute("orders", orders);
            
            System.out.println("✓ 找到 " + orders.size() + " 筆訂單");
            
            return "orders";
            
        } catch (Exception e) {
            System.out.println("❌ 查詢訂單列表失敗：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("orders", List.of());
            return "orders";
        }
    }

    /**
     * 取消訂單
     * URL: POST /orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, 
                             HttpSession session) {
        System.out.println("========== 取消訂單 ==========");
        System.out.println("訂單 ID：" + orderId);
        
        try {
            // 檢查是否登入
            Long customerId = SessionHelper.getCurrentCustomerId(session);
            if (customerId == null) {
            	try {
                    String error = URLEncoder.encode("請先登入", "UTF-8");
                    return "redirect:/login?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/login";
                }
            }
            
            // 先取得訂單，檢查是否屬於當前顧客
            Order order = orderService.getOrderById(orderId);
            if (!order.getCustomer().getCustNum().equals(customerId)) {
            	try {
                    String error = URLEncoder.encode("訂單不存在", "UTF-8");
                    return "redirect:/orders/my-orders?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/orders/my-orders";
                }
            }
            if (!order.getCustomer().getCustNum().equals(customerId)) {
                try {
                    String error = URLEncoder.encode("無權取消此訂單", "UTF-8");
                    return "redirect:/orders/my-orders?error=" + error;
                } catch (UnsupportedEncodingException e) {
                    return "redirect:/orders/my-orders";
                }
            }
            
            // 取消訂單
            orderService.cancelOrder(orderId);
            
            System.out.println("✓ 訂單取消成功");
            
            try {
                String message = URLEncoder.encode("訂單已取消", "UTF-8");
                return "redirect:/orders/" + orderId + "?message=" + message;
            } catch (UnsupportedEncodingException e) {
                return "redirect:/orders/" + orderId;
            }
        } catch (RuntimeException e) {
            System.out.println("❌ 取消訂單失敗：" + e.getMessage());
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/orders/" + orderId + "?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/orders/" + orderId;
            }
        }
    }
}