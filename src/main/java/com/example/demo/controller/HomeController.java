package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首頁 Controller
 * 處理首頁的請求，顯示所有產品
 */
@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 首頁
     * 顯示所有產品供使用者選購
     */
    @GetMapping("/")
    public String home(Model model) {
        // 取得所有產品
        model.addAttribute("products", productService.getAllProducts());
        return "home";  // 返回 home.html
    }
}