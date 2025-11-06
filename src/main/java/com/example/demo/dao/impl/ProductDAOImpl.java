package com.example.demo.dao.impl;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 產品 DAO 實作類別
 * 實作 ProductDAO 介面，提供產品的資料庫操作
 */
@Repository
public class ProductDAOImpl implements ProductDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 取得當前的 Hibernate Session
     * @return 當前 Session
     */
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    // 原有的方法實作

    @Override
    public List<Product> findAll() {
        return getCurrentSession()
                .createQuery("FROM Product", Product.class)
                .list();
    }

    @Override
    public Product findById(Long id) {
        return getCurrentSession().get(Product.class, id);
    }
    @Override
    public void save(Product product) {
        getCurrentSession().saveOrUpdate(product);
    }

    @Override
    public void delete(Long id) {
        Product product = getCurrentSession().get(Product.class, id);
        if (product != null) {
            getCurrentSession().delete(product);
        }
    }

    // 新增的搜尋和篩選方法實作

    /**
     * 根據關鍵字搜尋產品（模糊搜尋產品名稱）
     * 
     * 實作說明：
     * 1. 使用 HQL（Hibernate Query Language）
     * 2. 使用 LIKE 進行模糊搜尋
     * 3. 使用 LOWER() 函數進行不區分大小寫搜尋
     * 
     * SQL 等價語句：
     * SELECT * FROM products 
     * WHERE LOWER(prod_name) LIKE LOWER('%關鍵字%')
     * 
     * @param keyword 搜尋關鍵字
     * @return 符合條件的產品 List
     */
    @Override
    public List<Product> searchByKeyword(String keyword) {
        System.out.println("=== ProductDAO.searchByKeyword ===");
        System.out.println("關鍵字: " + keyword);
        
        // 建立 HQL 查詢
        // LOWER() 函數：將字串轉為小寫，實現不區分大小寫搜尋
        // LIKE :keyword：模糊搜尋
        String hql = "FROM Product p " +
                     "WHERE LOWER(p.prodName) LIKE LOWER(:keyword)";
        
        Query<Product> query = getCurrentSession().createQuery(hql, Product.class);
        
        // 設定參數，前後加上 % 實現模糊搜尋
        // 例如：keyword = "手機" → "%手機%"
        query.setParameter("keyword", "%" + keyword + "%");
        
        List<Product> results = query.list();
        System.out.println("✓ 找到 " + results.size() + " 項產品");
        
        return results;
    }

    /**
     * 根據產品類型查詢
     * 
     * 實作說明：
     * 使用精確匹配（= 運算符）
     * 
     * SQL 等價語句：
     * SELECT * FROM products WHERE prod_type = '類型'
     * 
     * @param type 產品類型
     * @return 符合該類型的產品 List
     */
    @Override
    public List<Product> findByType(String type) {
        System.out.println("=== ProductDAO.findByType ===");
        System.out.println("類型: " + type);
        
        // 建立 HQL 查詢
        // 使用 = 進行精確匹配
        String hql = "FROM Product p WHERE p.prodType = :type";
        
        Query<Product> query = getCurrentSession().createQuery(hql, Product.class);
        query.setParameter("type", type);
        
        List<Product> results = query.list();
        System.out.println("✓ 找到 " + results.size() + " 項產品");
        
        return results;
    }

    /**
     * 根據關鍵字和類型搜尋產品（組合條件）
     * 
     * 實作說明：
     * 1. 使用 AND 連接多個條件
     * 2. 同時滿足關鍵字模糊搜尋和類型精確匹配
     * 
     * SQL 等價語句：
     * SELECT * FROM products 
     * WHERE LOWER(prod_name) LIKE LOWER('%關鍵字%') 
     * AND prod_type = '類型'
     * 
     * @param keyword 搜尋關鍵字
     * @param type 產品類型
     * @return 符合條件的產品 List
     */
    @Override
    public List<Product> searchByKeywordAndType(String keyword, String type) {
        System.out.println("=== ProductDAO.searchByKeywordAndType ===");
        System.out.println("關鍵字: " + keyword);
        System.out.println("類型: " + type);
        
        // 建立 HQL 查詢
        // 使用 AND 連接多個條件
        String hql = "FROM Product p " +
                     "WHERE LOWER(p.prodName) LIKE LOWER(:keyword) " +
                     "AND p.prodType = :type";
        
        Query<Product> query = getCurrentSession().createQuery(hql, Product.class);
        
        // 設定參數
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("type", type);
        
        List<Product> results = query.list();
        System.out.println("✓ 找到 " + results.size() + " 項產品");
        
        return results;
    }
    /**
     * 取得所有不重複的產品類型
     * 
     * 實作說明：
     * 1. 使用 SELECT DISTINCT 取得不重複的類型
     * 2. 使用 ORDER BY 排序（方便使用者選擇）
     * 3. 過濾掉 NULL 值
     * 
     * HQL 說明：
     * - DISTINCT: 去除重複值
     * - IS NOT NULL: 過濾空值
     * - ORDER BY: 按字母順序排序
     * 
     * @return 產品類型的 List
     */
    @Override
    public List<String> findAllProductTypes() {
        System.out.println("=== ProductDAO.findAllProductTypes ===");
        
        // 建立 HQL 查詢
        // SELECT DISTINCT: 只選擇不重複的類型
        // p.prodType: 選擇產品類型欄位
        // WHERE p.prodType IS NOT NULL: 過濾掉空值
        // ORDER BY p.prodType: 按字母順序排序
        String hql = "SELECT DISTINCT p.prodType " +
                     "FROM Product p " +
                     "WHERE p.prodType IS NOT NULL " +
                     "ORDER BY p.prodType";
        
        Query<String> query = getCurrentSession().createQuery(hql, String.class);
        List<String> types = query.list();
        
        System.out.println("✓ 找到 " + types.size() + " 種產品類型：" + types);
        
        return types;
    }
}