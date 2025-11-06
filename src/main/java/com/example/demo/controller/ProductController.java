package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.example.demo.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;

import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.math.BigDecimal;
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
    
    // 注入 ServletContext 用於取得檔案上傳路徑
    @Autowired
    private ServletContext servletContext;

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
            
            // 根據參數決定查詢方式
            
            // 有搜尋關鍵字或類型篩選
            if ((keyword != null && !keyword.trim().isEmpty()) || 
                (type != null && !type.trim().isEmpty())) {
                
                System.out.println("✓ 執行搜尋/篩選");
                
                // 呼叫 Service 的搜尋方法
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
            
         // 加入產品類型列表到 Model
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
    
    /**
     * 顯示新增產品表單
     * URL: GET /products/add
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
    	System.out.println("========== 顯示新增產品表單 ==========");
        model.addAttribute("product", new Product());
        return "add-product";
    }

    /**
     * 顯示編輯產品表單
     * URL: GET /products/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        System.out.println("========== 顯示編輯產品表單 ==========");
        System.out.println("產品 ID: " + id);
        
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            
            System.out.println("✓ 找到產品：" + product.getProdName());
            
            return "edit-product";
            
        } catch (Exception e) {
            System.out.println("❌ 查詢產品時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return "redirect:/products";
        }
    }

    /**
    * 處理新增產品（含檔案上傳）
    * 
    * 功能說明：
    * 1. 接收表單資料和上傳的圖片檔案
    * 2. 如果有上傳圖片，儲存到指定目錄
    * 3. 將產品資料儲存到資料庫
    * 
    * URL: POST /products/save
    * 
    * 參數說明：
    * @param prodName 產品名稱
    * @param prodType 產品類型
    * @param prodPrice 產品價格
    * @param prodLine 產品線
    * @param prodDescription 產品描述
    * @param imageFile 上傳的圖片檔案（可選）
    * @param model Spring MVC Model
    * @return 重導向到產品列表
    */
   @PostMapping("/save")
   public String saveProduct(
		   @ModelAttribute Product product,
           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
           Model model) {
       
       System.out.println("========== 新增產品 ==========");
       System.out.println("產品描述長度: " + (product.getProdDescription() != null ? product.getProdDescription().length() : 0));
       System.out.println("有上傳檔案: " + (imageFile != null && !imageFile.isEmpty()));
       
       try {        
           // 處理圖片上傳 
           if (imageFile != null && !imageFile.isEmpty()) {
               // 取得上傳目錄的實際路徑（使用 Tomcat webapps 目錄）
               String uploadPath = servletContext.getRealPath("/resources/images/products/");
               System.out.println("✓ 上傳路徑：" + uploadPath);
               
               // 確保目錄存在
               File uploadDir = new File(uploadPath);
               if (!uploadDir.exists()) {
                   boolean created = uploadDir.mkdirs();
                   if (created) {
                       System.out.println("✓ 建立上傳目錄：" + uploadPath);
                   }
               }
               
               // 儲存檔案並取得新檔名
               String savedFilename = FileUploadUtil.saveFile(imageFile, uploadPath);
               
               // 儲存完整的相對路徑到資料庫
               String imageUrl = "/resources/images/products/" + savedFilename;
               product.setProdImage(imageUrl);
               
               System.out.println("✓ 圖片已儲存：" + savedFilename);
               System.out.println("✓ 資料庫儲存 URL：" + imageUrl);
           } else {
               System.out.println("○ 未上傳圖片");
           }
           
           // 儲存產品到資料庫
           productService.saveProduct(product);
           
           System.out.println("產品新增成功");
           System.out.println("====================================");
           
           return "redirect:/products";
           
       } catch (IllegalArgumentException e) {
           // 檔案驗證失敗（例如：格式不正確、檔案太大）
           System.out.println("❌ 檔案驗證失敗：" + e.getMessage());
           model.addAttribute("error", e.getMessage());
           model.addAttribute("product", new Product());
           return "add-product";
           
       } catch (Exception e) {
           // 其他錯誤
           System.out.println("❌ 新增產品失敗：" + e.getMessage());
           e.printStackTrace();
           model.addAttribute("error", "新增產品時發生錯誤：" + e.getMessage());
           model.addAttribute("product", new Product());
           return "add-product";
       }
   }

   // 處理新增產品
   @PostMapping("/update/{id}")
   public String updateProduct(
           @PathVariable("id") Long id,
           @ModelAttribute Product product,
           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
           @RequestParam(value = "oldImage", required = false) String oldImage,
           Model model) {
       
       System.out.println("========== 更新產品 ==========");
       System.out.println("產品 ID: " + id);
       //System.out.println("產品名稱: " + prodName);
       System.out.println("有上傳新檔案: " + (imageFile != null && !imageFile.isEmpty()));
       System.out.println("舊圖片: " + oldImage);
       
       try {
    	// 設定產品 ID 
           // 因為表單沒有 prodNum 欄位，所以需要手動設定
           product.setProdNum(id);
           
           // 處理圖片上傳
           if (imageFile != null && !imageFile.isEmpty()) {
               // 有上傳新圖片
               
               // 取得上傳目錄的實際路徑
               String uploadPath = servletContext.getRealPath("/resources/images/products/");
               
               // 刪除舊圖片（如果舊圖片是完整路徑，需要提取檔名）
               if (oldImage != null && !oldImage.isEmpty()) {
                   // 從 URL 中提取檔名：/resources/images/products/abc.jpg → abc.jpg
                   String oldFilename = oldImage.substring(oldImage.lastIndexOf('/') + 1);
                   FileUploadUtil.deleteFile(oldFilename, uploadPath);
               }
               
               // 儲存新圖片
               String savedFilename = FileUploadUtil.saveFile(imageFile, uploadPath);
               
               // ⭐ 重點：儲存完整的相對路徑到資料庫
               String imageUrl = "/resources/images/products/" + savedFilename;
               product.setProdImage(imageUrl);
               
               System.out.println("✓ 新圖片已儲存：" + savedFilename);
               System.out.println("✓ 資料庫儲存 URL：" + imageUrl);
               
           } else {
               // 沒有上傳新圖片，保留舊圖片（已經是完整 URL）
               product.setProdImage(oldImage);
               System.out.println("○ 保留舊圖片 URL：" + oldImage);
           }
           
           // 更新產品到資料庫
           productService.updateProduct(id, product);
           
           System.out.println("✓ 產品更新成功");
           System.out.println("====================================");
           
           return "redirect:/products";
           
       } catch (IllegalArgumentException e) {
           System.out.println("❌ 檔案驗證失敗：" + e.getMessage());
           model.addAttribute("error", e.getMessage());
           model.addAttribute("product", productService.getProductById(id));
           return "edit-product";
           
       } catch (Exception e) {
           System.out.println("❌ 更新產品失敗：" + e.getMessage());
           e.printStackTrace();
           model.addAttribute("error", "更新產品時發生錯誤：" + e.getMessage());
           
           // 重新取得產品資料以顯示在表單
           try {
               Product existingProduct = productService.getProductById(id);
               model.addAttribute("product", existingProduct);
           } catch (Exception ex) {
               model.addAttribute("product", product);
           }
           
           return "edit-product";
       }
   }
   
   /**
   * 刪除產品
   * 
   * URL: GET /products/delete/{id}
   */
  @GetMapping("/delete/{id}")
  public String deleteProduct(@PathVariable("id") Long id) {
      System.out.println("========== 刪除產品 ==========");
      System.out.println("產品 ID: " + id);
      
      try {
          // 刪除產品前，先取得圖片檔名以便刪除圖片檔案
          Product product = productService.getProductById(id);
          String imageName = product.getProdImage();
          
          // 刪除產品資料
          productService.deleteProduct(id);
          
          // 刪除圖片檔案
          if (imageName != null && !imageName.isEmpty()) {
              String uploadPath = servletContext.getRealPath("/resources/images/products/");
              
              // 從 URL 中提取檔名：/resources/images/products/abc.jpg → abc.jpg
              String filename = imageName.substring(imageName.lastIndexOf('/') + 1);
              
              FileUploadUtil.deleteFile(filename, uploadPath);
              System.out.println("✓ 已刪除圖片檔案：" + filename);
          }
          
          System.out.println("✓ 產品刪除成功");
          System.out.println("====================================");
          
      } catch (Exception e) {
          System.out.println("❌ 刪除產品時發生錯誤：" + e.getMessage());
          e.printStackTrace();
      }
      
      return "redirect:/products";
  }

    // 顯示商品詳細頁面
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        System.out.println("========== 商品詳細頁面請求 ==========");
        System.out.println("商品 ID: " + id);
        
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            
            System.out.println("✓ 找到商品：" + product.getProdName());
            
            return "product-detail";
            
        } catch (Exception e) {
            System.out.println("❌ 查詢商品時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return "redirect:/products";
        }
    }
}