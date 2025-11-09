package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
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
 * 管理員後台 Controller
 * 處理管理員相關的 HTTP 請求
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    /**
     * 顯示管理員儀表板
     * URL: GET /admin/dashboard
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        System.out.println("========== 顯示管理員儀表板 ==========");
        
        try {
            // 檢查是否為管理員（這裡簡化處理，實際應該檢查管理員權限）
            // 你可以根據自己的需求調整權限檢查邏輯
            
            // 取得統計資料
            long totalOrders = orderService.getTotalOrderCount();
            long totalProducts = productService.getTotalProductCount();
            long totalCustomers = customerService.getTotalCustomerCount();
            
            // 取得最近的 5 筆訂單
            List<Order> recentOrders = orderService.getRecentOrders(5);
            
            // 傳遞資料到前端
            model.addAttribute("adminName", "管理員"); // 可以從 session 取得管理員名稱
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("totalCustomers", totalCustomers);
            model.addAttribute("recentOrders", recentOrders);
            
            System.out.println("✓ 訂單總數：" + totalOrders);
            System.out.println("✓ 商品總數：" + totalProducts);
            System.out.println("✓ 客戶總數：" + totalCustomers);
            
            return "admin/dashboard";
            
        } catch (Exception e) {
            System.out.println("❌ 顯示儀表板時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "admin/dashboard";
        }
    }

    /**
     * 顯示訂單管理頁面
     * URL: GET /admin/orders
     */
    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public String showOrderManagement(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {
        
        System.out.println("========== 顯示訂單管理頁面 ==========");
        System.out.println("狀態篩選：" + status);
        System.out.println("搜尋關鍵字：" + search);
        
        try {
            List<Order> orders;
            
            // 根據條件篩選訂單
            if (status != null && !status.isEmpty()) {
                // 根據狀態篩選
                orders = orderService.getOrdersByStatus(status);
                System.out.println("✓ 找到 " + orders.size() + " 筆「" + status + "」狀態的訂單");
            } else if (search != null && !search.isEmpty()) {
                // 根據關鍵字搜尋（訂單編號或客戶名稱）
                orders = orderService.searchOrders(search);
                System.out.println("✓ 找到 " + orders.size() + " 筆符合「" + search + "」的訂單");
            } else {
                // 取得所有訂單
                orders = orderService.getAllOrders();
                System.out.println("✓ 找到 " + orders.size() + " 筆訂單");
            }
            
            // 傳遞資料到前端
            model.addAttribute("orders", orders);
            model.addAttribute("currentStatus", status);
            model.addAttribute("searchKeyword", search);
            
            return "admin/orders";
            
        } catch (Exception e) {
            System.out.println("❌ 查詢訂單時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("orders", List.of());
            return "admin/orders";
        }
    }

    /**
     * 更新訂單狀態
     * URL: POST /admin/orders/{orderId}/update-status
     */
    @PostMapping("/orders/{orderId}/update-status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus) {
        
        System.out.println("========== 更新訂單狀態 ==========");
        System.out.println("訂單 ID：" + orderId);
        System.out.println("新狀態：" + newStatus);
        
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            System.out.println("✓ 訂單狀態更新成功");
            
         // 使用 URL 編碼處理中文訊息
            String message = URLEncoder.encode("訂單狀態更新成功", "UTF-8");
            return "redirect:/admin/orders?message=" + message;
            
        } catch (UnsupportedEncodingException e) {
            System.out.println("❌ URL 編碼失敗：" + e.getMessage());
            return "redirect:/admin/orders";
        } catch (Exception e) {
            System.out.println("❌ 更新訂單狀態失敗：" + e.getMessage());
            e.printStackTrace();
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/admin/orders?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/admin/orders";
            }
        }
    }

    /**
     * 刪除訂單（實際上是取消訂單）
     * URL: POST /admin/orders/{orderId}/delete
     */
    @PostMapping("/orders/{orderId}/delete")
    public String deleteOrder(@PathVariable Long orderId) {
        System.out.println("========== 刪除訂單 ==========");
        System.out.println("訂單 ID：" + orderId);
        
        try {
            orderService.cancelOrder(orderId);
            System.out.println("✓ 訂單已取消");
            
         // 使用 URL 編碼處理中文訊息
            String message = URLEncoder.encode("訂單已取消", "UTF-8");
            return "redirect:/admin/orders?message=" + message;
            
        } catch (UnsupportedEncodingException e) {
            System.out.println("❌ URL 編碼失敗：" + e.getMessage());
            return "redirect:/admin/orders";
        } catch (Exception e) {
            System.out.println("❌ 刪除訂單失敗：" + e.getMessage());
            try {
                String error = URLEncoder.encode(e.getMessage(), "UTF-8");
                return "redirect:/admin/orders?error=" + error;
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/admin/orders";
            }
        }
    }
}