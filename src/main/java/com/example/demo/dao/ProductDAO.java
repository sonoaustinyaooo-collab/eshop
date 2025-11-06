package com.example.demo.dao;

import com.example.demo.model.Product;
import java.util.List;

/**
 * 產品 DAO 介面
 * 定義產品的資料庫操作方法
 */
public interface ProductDAO {
    
    // 原有的方法
    
    /**
     * 查詢所有產品
     * @return 所有產品的 List
     */
    List<Product> findAll();
    
    /**
     * 根據 ID 查詢產品
     * @param id 產品 ID
     * @return Product 物件，如果不存在則返回 null
     */
    Product findById(Long id);
    
    /**
     * 儲存或更新產品
     * @param product 產品物件
     */
    void save(Product product);
    
    /**
     * 刪除產品
     * @param id 產品 ID
     */
    void delete(Long id);
    
    // 新增的搜尋和篩選方法
    
    /**
     * 根據關鍵字搜尋產品（模糊搜尋產品名稱）
     * 
     * 功能說明：
     * 使用 LIKE 語句搜尋產品名稱
     * 例如：keyword = "手機" 會找到 "智慧型手機"、"手機殼" 等
     * 
     * @param keyword 搜尋關鍵字
     * @return 符合條件的產品 List
     */
    List<Product> searchByKeyword(String keyword);
    
    /**
     * 根據產品類型查詢
     * 
     * @param type 產品類型（例如：電子產品、服飾、食品）
     * @return 符合該類型的產品 List
     */
    List<Product> findByType(String type);
    
    /**
     * 根據關鍵字和類型搜尋產品（組合條件）
     * 
     * 功能說明：
     * 同時滿足關鍵字和類型的產品
     * 
     * @param keyword 搜尋關鍵字
     * @param type 產品類型
     * @return 符合條件的產品 List
     */
    List<Product> searchByKeywordAndType(String keyword, String type);
    
    /**
     * 取得所有不重複的產品類型
     * 
     * 用途：提供給下拉選單使用
     * 
     * SQL 等價語句：
     * SELECT DISTINCT prod_type FROM products ORDER BY prod_type
     * 
     * @return 所有產品類型的 List（已排序，無重複）
     */
    List<String> findAllProductTypes();    
    
}