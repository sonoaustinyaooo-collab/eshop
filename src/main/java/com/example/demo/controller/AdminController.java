package com.example.demo.controller;

import com.example.demo.model.Order;  
import com.example.demo.model.Product;  
import com.example.demo.model.Customer;  
import com.example.demo.model.User;  
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.CustomerService;
import com.example.demo.util.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;  // ⭐ 加入這行
import java.util.stream.Collectors;  // ⭐ 加入這行

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        
        System.out.println("=== 進入管理員儀表板 ===");
        
        // 權限檢查
        if (!SessionHelper.isAdmin(session)) {
            System.out.println("❌ 權限不足，不是管理員");
            return "redirect:/login?error=login_required";
        }
        
        System.out.println("✓ 管理員權限驗證通過");
        
        try {
            // 改用明確的類型宣告
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("totalOrders", orders.size());
            System.out.println("訂單總數：" + orders.size());
            
            List<Product> products = productService.getAllProducts();
            model.addAttribute("totalProducts", products.size());
            System.out.println("商品總數：" + products.size());
            
            List<Customer> customers = customerService.getAllCustomers();
            model.addAttribute("totalCustomers", customers.size());
            System.out.println("客戶總數：" + customers.size());
            
            // 改用 .collect(Collectors.toList())
            List<Order> recentOrders = orders.stream()
                .limit(10)
                .collect(Collectors.toList());  // 相容 Java 8+
            
            model.addAttribute("recentOrders", recentOrders);
            System.out.println("最近訂單數：" + recentOrders.size());
            
            // 改用明確的類型
            User admin = SessionHelper.getLoggedInAdmin(session);
            
            if (admin != null) {
                model.addAttribute("adminName", admin.getName());
                System.out.println("管理員名稱：" + admin.getName());
            } else {
                model.addAttribute("adminName", "管理員");
                System.out.println("⚠ 使用預設管理員名稱");
            }
            
            System.out.println("✓ 準備返回視圖：admin/dashboard");
            return "admin/dashboard";
            
        } catch (Exception e) {
            System.out.println("❌ 發生錯誤：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "載入儀表板資料失敗：" + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/orders")
    public String manageOrders(HttpSession session, Model model) {
        
        System.out.println("=== 進入訂單管理頁面 ===");
        
        if (!SessionHelper.isAdmin(session)) {
            System.out.println("❌ 權限不足");
            return "redirect:/login?error=login_required";
        }
        
        try {
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);
            
            System.out.println("✓ 載入 " + orders.size() + " 筆訂單");
            
            return "admin/orders";
            
        } catch (Exception e) {
            System.out.println("❌ 載入訂單失敗：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "載入訂單資料失敗");
            return "error";
        }
    }
    
    @GetMapping("/orders/{orderId}")
    public String viewOrderDetail(@PathVariable Long orderId,
                                 HttpSession session,
                                 Model model) {
        
        System.out.println("=== 查看訂單詳情：" + orderId + " ===");
        
        if (!SessionHelper.isAdmin(session)) {
            return "redirect:/login?error=login_required";
        }
        
        try {
            Order order = orderService.getOrderById(orderId);
            
            if (order == null) {
                model.addAttribute("error", "找不到訂單");
                return "error";
            }
            
            model.addAttribute("order", order);
            
            System.out.println("✓ 訂單編號：" + order.getOrderNumber());
            
            return "order-detail";
            
        } catch (Exception e) {
            System.out.println("❌ 載入訂單詳情失敗：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "載入訂單詳情失敗");
            return "error";
        }
    }
    
    @PostMapping("/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                   @RequestParam String status,
                                   HttpSession session) {
        
        System.out.println("=== 更新訂單狀態 ===");
        System.out.println("訂單 ID：" + orderId);
        System.out.println("新狀態：" + status);
        
        if (!SessionHelper.isAdmin(session)) {
            return "redirect:/login?error=login_required";
        }
        
        try {
            orderService.updateOrderStatus(orderId, status);
            
            System.out.println("✓ 訂單狀態更新成功");
            
            return "redirect:/admin/orders/" + orderId;
            
        } catch (Exception e) {
            System.out.println("❌ 更新訂單狀態失敗：" + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/orders/" + orderId + "?error=更新失敗";
        }
    }
}