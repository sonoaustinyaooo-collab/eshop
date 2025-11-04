package com.example.demo.service.impl;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 產品 Service 實作類別
 * 實作 ProductService 介面定義的所有方法
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDAO productDAO;

    // ===== 原有的方法實作 =====
    
    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new RuntimeException("找不到產品，ID: " + id);
        }
        return product;
    }

    @Override
    public void saveProduct(Product product) {
        productDAO.save(product);
    }

    @Override
    public void updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productDAO.findById(id);
        if (existingProduct != null) {
            existingProduct.setProdName(updatedProduct.getProdName());
            existingProduct.setProdType(updatedProduct.getProdType());
            existingProduct.setProdPrice(updatedProduct.getProdPrice());
            existingProduct.setProdLine(updatedProduct.getProdLine());
            productDAO.save(existingProduct);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        productDAO.delete(id);
    }

    // ===== ⭐ 新增的搜尋和篩選方法實作 =====
    
    /**
     * 搜尋產品（支援關鍵字、類型、排序）
     * 
     * 實作邏輯：
     * 1. 根據有無關鍵字和類型，呼叫不同的 DAO 方法
     * 2. 對查詢結果進行排序
     * 
     * @param keyword 搜尋關鍵字
     * @param type 產品類型
     * @param sort 排序方式
     * @return 符合條件的產品 List
     */
    @Override
    public List<Product> searchProducts(String keyword, String type, String sort) {
        System.out.println("=== ProductService.searchProducts ===");
        System.out.println("關鍵字: " + keyword);
        System.out.println("類型: " + type);
        System.out.println("排序: " + sort);
        
        List<Product> products;
        
        // ===== 步驟1：根據條件查詢產品 =====
        
        // 情況A：有關鍵字 + 有類型
        if (isNotEmpty(keyword) && isNotEmpty(type)) {
            System.out.println("→ 呼叫 DAO: 關鍵字 + 類型篩選");
            products = productDAO.searchByKeywordAndType(keyword, type);
        }
        // 情況B：只有關鍵字
        else if (isNotEmpty(keyword)) {
            System.out.println("→ 呼叫 DAO: 關鍵字搜尋");
            products = productDAO.searchByKeyword(keyword);
        }
        // 情況C：只有類型
        else if (isNotEmpty(type)) {
            System.out.println("→ 呼叫 DAO: 類型篩選");
            products = productDAO.findByType(type);
        }
        // 情況D：沒有篩選條件
        else {
            System.out.println("→ 呼叫 DAO: 取得所有產品");
            products = productDAO.findAll();
        }
        
        System.out.println("✓ DAO 返回 " + products.size() + " 項產品");
        
        // ===== 步驟2：排序 =====
        if (isNotEmpty(sort)) {
            products = sortProducts(products, sort);
            System.out.println("✓ 已依 " + sort + " 排序");
        }
        
        return products;
    }
    
    /**
     * 取得所有產品並排序
     * 
     * @param sort 排序方式
     * @return 排序後的產品 List
     */
    @Override
    public List<Product> getAllProductsSorted(String sort) {
        List<Product> products = productDAO.findAll();
        
        if (isNotEmpty(sort)) {
            products = sortProducts(products, sort);
        }
        
        return products;
    }
    
    // ===== 輔助方法 =====
    
    /**
     * 檢查字串是否不為空
     * 
     * @param str 要檢查的字串
     * @return true 如果字串不為 null 且不為空白
     */
    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * 對產品列表進行排序
     * 
     * 排序方式：
     * - price_asc: 價格由低到高
     * - price_desc: 價格由高到低
     * - name_asc: 名稱 A-Z
     * - name_desc: 名稱 Z-A
     * 
     * @param products 要排序的產品列表
     * @param sort 排序方式
     * @return 排序後的產品列表
     */
    private List<Product> sortProducts(List<Product> products, String sort) {
        
        // 使用 Java Stream API 進行排序
        switch (sort) {
            case "price_asc":
                // 價格由低到高
                return products.stream()
                        .sorted(Comparator.comparing(Product::getProdPrice))
                        .collect(Collectors.toList());
                
            case "price_desc":
                // 價格由高到低
                return products.stream()
                        .sorted(Comparator.comparing(Product::getProdPrice).reversed())
                        .collect(Collectors.toList());
                
            case "name_asc":
                // 名稱 A-Z
                return products.stream()
                        .sorted(Comparator.comparing(Product::getProdName))
                        .collect(Collectors.toList());
                
            case "name_desc":
                // 名稱 Z-A
                return products.stream()
                        .sorted(Comparator.comparing(Product::getProdName).reversed())
                        .collect(Collectors.toList());
                
            default:
                // 不排序，返回原列表
                return products;
        }
    }
    
    /**
     * ⭐ 取得所有產品類型
     * 
     * @return 產品類型的 List
     */
    @Override
    public List<String> getAllProductTypes() {
        return productDAO.findAllProductTypes();
    }
}