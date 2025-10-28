package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.util.SessionHelper;  // 引入 SessionHelper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

/**
 * 登入控制器（更新版）
 * 使用 SessionHelper 管理 Session
 */
@Controller
public class LoginController {

    @Autowired
    private AuthService authService;
    
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String message,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", error);
        }
        
        if (logout != null) {
            model.addAttribute("message", "您已成功登出");
        }
        
        if (message != null) {
            model.addAttribute("message", message);
        }
        
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String userType,
            HttpSession session) {
        
        try {
            if (userType.equals("admin")) {
                // ===== 管理員登入 =====
                User user = authService.adminLogin(username, password);
                
                if (user != null) {
                    // 登入成功，使用 SessionHelper 設定 Session
                    SessionHelper.setAdminSession(session, user);
                    
                    // 導向管理員儀表板
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/login?error=帳號或密碼錯誤";
                }
                
            } else if (userType.equals("customer")) {
                // ===== 顧客登入 =====
                Customer customer = authService.customerLogin(username, password);
                
                if (customer != null) {
                    // 登入成功，使用 SessionHelper 設定 Session
                    SessionHelper.setCustomerSession(session, customer);
                    
                    // 導向首頁
                    return "redirect:/";
                } else {
                    return "redirect:/login?error=帳號或密碼錯誤";
                }
                
            } else {
                return "redirect:/login?error=無效的使用者類型";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error=登入失敗，請稍後再試";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 使用 SessionHelper 登出
        SessionHelper.logout(session);
        
        return "redirect:/login?logout=true";
    }
    
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }
    
    @PostMapping("/register")
    public String processRegister(@ModelAttribute Customer customer, Model model) {
        try {
            authService.registerCustomer(customer);
            return "redirect:/login?message=註冊成功，請登入";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customer);
            return "register";
        }
    }
    
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return !authService.isUsernameExist(username);
    }
}