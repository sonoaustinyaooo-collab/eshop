package com.example.demo.dao.impl;

import com.example.demo.dao.CartDAO;
import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 購物車 DAO 實作類別
 * 使用 Hibernate Session 進行資料庫操作
 */
@Repository
public class CartDAOImpl implements CartDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * 取得當前 Hibernate Session
     */
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public Cart findById(Long id) {
        return getCurrentSession().get(Cart.class, id);
    }
    
    @Override
    public Cart findByCustomer(Customer customer) {
        // 使用 HQL 查詢特定客戶的購物車
        Query<Cart> query = getCurrentSession()
            .createQuery("FROM Cart WHERE customer = :customer", Cart.class)
            .setParameter("customer", customer);
        
        // 回傳唯一結果，如果沒有則回傳 null
        return query.uniqueResult();
    }
    
    @Override
    public void save(Cart cart) {
        // saveOrUpdate 會自動判斷是新增還是更新
        getCurrentSession().saveOrUpdate(cart);
    }
    
    @Override
    public void delete(Long id) {
        Cart cart = getCurrentSession().get(Cart.class, id);
        if (cart != null) {
            getCurrentSession().delete(cart);
        }
    }
}