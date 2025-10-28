package com.example.demo.dao.impl;

import com.example.demo.dao.CartItemDAO;
import com.example.demo.model.CartItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 購物車項目 DAO 實作類別
 */
@Repository
public class CartItemDAOImpl implements CartItemDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public CartItem findById(Long id) {
        return getCurrentSession().get(CartItem.class, id);
    }
    
    @Override
    public void save(CartItem cartItem) {
        getCurrentSession().saveOrUpdate(cartItem);
    }
    
    @Override
    public void delete(Long id) {
        CartItem cartItem = getCurrentSession().get(CartItem.class, id);
        if (cartItem != null) {
            getCurrentSession().delete(cartItem);
        }
    }
}