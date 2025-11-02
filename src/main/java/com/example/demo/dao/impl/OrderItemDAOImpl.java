package com.example.demo.dao.impl;

import com.example.demo.dao.OrderItemDAO;
import com.example.demo.model.OrderItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OrderItemDAOImpl implements OrderItemDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public List<OrderItem> findAll() {
        return getCurrentSession()
            .createQuery("FROM OrderItem", OrderItem.class)
            .list();
    }
    
    @Override
    public OrderItem findById(Long id) {
        return getCurrentSession().get(OrderItem.class, id);
    }
    
    @Override
    public void save(OrderItem orderItem) {
        getCurrentSession().saveOrUpdate(orderItem);
    }
    
    @Override
    public void delete(Long id) {
        OrderItem orderItem = getCurrentSession().get(OrderItem.class, id);
        if (orderItem != null) {
            getCurrentSession().delete(orderItem);
        }
    }
    
    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        Query<OrderItem> query = getCurrentSession()
            .createQuery("FROM OrderItem WHERE order.orderId = :orderId", OrderItem.class)
            .setParameter("orderId", orderId);
        return query.list();
    }
}