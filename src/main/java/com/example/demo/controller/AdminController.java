package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.CustomerService;
import com.example.demo.util.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")  
public class AdminController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping("/dashboard")  // 確認路徑是 /dashboard
    public String showDashboard(HttpSession session, Model model) {
        
        // 權限檢查
        if (!SessionHelper.isAdmin(session)) {
            return "redirect:/login?error=login_required";
        }
        
        try {
            // 取得統計資料
            var orders = orderService.getAllOrders();
            model.addAttribute("totalOrders", orders.size());
            
            var products = productService.getAllProducts();
            model.addAttribute("totalProducts", products.size());
            
            var customers = customerService.getAllCustomers();
            model.addAttribute("totalCustomers", customers.size());
            
            // 取得最近 10 筆訂單
            var recentOrders = orders.stream()
                .limit(10)
                .toList();
            model.addAttribute("recentOrders", recentOrders);
            
            // 從 Session 取得管理員資訊
            var admin = SessionHelper.getLoggedInAdmin(session);
            if (admin != null) {
                model.addAttribute("adminName", admin.getName());
            } else {
                model.addAttribute("adminName", "管理員");
            }
            
            return "admin/dashboard";  // 確認返回 admin/dashboard
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "載入儀表板資料失敗：" + e.getMessage());
            return "error";
        }
    }
}