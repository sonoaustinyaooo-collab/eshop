package com.example.demo.service;

import com.example.demo.model.Product;
import java.util.List;

/**
 * 產品 Service 介面
 * 定義產品相關的業務邏輯方法
 */
public interface ProductService {
    
    // ===== 原有的方法 =====
    
    /**
     * 查詢所有產品
     * @return 所有產品的 List
     */
    List<Product> getAllProducts();
    
    /**
     * 根據 ID 查詢產品
     * @param id 產品 ID
     * @return Product 物件
     */
    Product getProductById(Long id);
    
    /**
     * 儲存產品
     * @param product 產品物件
     */
    void saveProduct(Product product);
    
    /**
     * 更新產品
     * @param id 產品 ID
     * @param product 更新的產品資料
     */
    void updateProduct(Long id, Product product);
    
    /**
     * 刪除產品
     * @param id 產品 ID
     */
    void deleteProduct(Long id);
    
    // ===== ⭐ 新增的搜尋和篩選方法 =====
    
    /**
     * 搜尋產品（支援關鍵字、類型、排序）
     * 
     * 功能說明：
     * 1. 根據關鍵字搜尋產品名稱（模糊搜尋）
     * 2. 根據類型篩選產品
     * 3. 根據排序方式排序結果
     * 
     * @param keyword 搜尋關鍵字（可為 null）
     * @param type 產品類型（可為 null）
     * @param sort 排序方式（price_asc, price_desc, name_asc, name_desc，可為 null）
     * @return 符合條件的產品 List
     */
    List<Product> searchProducts(String keyword, String type, String sort);
    
    /**
     * 取得所有產品並排序
     * 
     * @param sort 排序方式（price_asc, price_desc, name_asc, name_desc）
     * @return 排序後的產品 List
     */
    List<Product> getAllProductsSorted(String sort);
    
    /**
     * ⭐ 取得所有產品類型
     * 
     * @return 產品類型的 List
     */
    List<String> getAllProductTypes();
}