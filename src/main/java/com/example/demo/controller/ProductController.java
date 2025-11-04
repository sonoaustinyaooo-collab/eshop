package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 產品控制器
 * 處理所有產品相關的 HTTP 請求
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * ⭐ 顯示產品列表（支援搜尋和篩選）
     * 
     * 功能說明：
     * 1. 接收搜尋關鍵字、類型篩選、排序方式等參數
     * 2. 根據參數查詢產品
     * 3. 返回產品列表頁面
     * 
     * URL: GET /products
     * URL 範例：
     * - /products （顯示所有產品）
     * - /products?keyword=手機 （搜尋「手機」）
     * - /products?type=電子產品 （篩選電子產品）
     * - /products?sort=price_asc （價格由低到高排序）
     * - /products?keyword=手機&type=電子產品&sort=price_asc （組合條件）
     * 
     * @param keyword 搜尋關鍵字（可選，用於搜尋產品名稱）
     * @param type 產品類型（可選，用於篩選類型）
     * @param sort 排序方式（可選，price_asc/price_desc/name_asc/name_desc）
     * @param model Spring MVC Model，用於傳遞資料到視圖
     * @return 產品列表頁面
     */
     
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,  // 接收搜尋關鍵字參數
            @RequestParam(required = false) String type,     // 接收產品類型參數
            @RequestParam(required = false) String sort,     // 接收排序方式參數
            Model model) {
        
        System.out.println("========== 產品列表請求 ==========");
        System.out.println("搜尋關鍵字: " + keyword);
        System.out.println("產品類型: " + type);
        System.out.println("排序方式: " + sort);
        
        try {
            List<Product> products;
            
            // ===== 根據參數決定查詢方式 =====
            
            // 有搜尋關鍵字或類型篩選
            if ((keyword != null && !keyword.trim().isEmpty()) || 
                (type != null && !type.trim().isEmpty())) {
                
                System.out.println("✓ 執行搜尋/篩選");
                
                // ⭐ 呼叫 Service 的搜尋方法
                products = productService.searchProducts(keyword, type, sort);
                
            } else {
                // 沒有任何篩選條件，顯示所有產品
                System.out.println("✓ 顯示所有產品");
                
                // 根據排序方式取得產品
                if (sort != null && !sort.trim().isEmpty()) {
                    products = productService.getAllProductsSorted(sort);
                } else {
                    products = productService.getAllProducts();
                }
            }
            
         // ===== 加入產品類型列表到 Model =====
            List<String> productTypes = productService.getAllProductTypes();
            model.addAttribute("productTypes", productTypes);
            System.out.println("✓ 產品類型：" + productTypes);
            
            // 將結果加入 Model 
            model.addAttribute("products", products);
            
            System.out.println("✓ 找到 " + products.size() + " 項產品");
            System.out.println("====================================");
            
            return "products";
            
        } catch (Exception e) {
            System.out.println("❌ 查詢產品時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            
            // 發生錯誤時返回空列表
            model.addAttribute("products", List.of());
            model.addAttribute("error", "查詢產品時發生錯誤");
            
            return "products";
        }
    }  
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "edit-product"; // 對應 /WEB-INF/views/edit-product.html
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") Product product) {
        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}