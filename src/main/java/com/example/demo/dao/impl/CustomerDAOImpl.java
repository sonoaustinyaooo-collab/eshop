package com.example.demo.dao.impl;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CustomerDAOImpl implements CustomerDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public List<Customer> findAll() {
        return getCurrentSession().createQuery("FROM Customer", Customer.class).list();
    }
    
    @Override
    public Customer findById(Long id) {
        return getCurrentSession().get(Customer.class, id);
    }
    
    @Override
    public void save(Customer customer) {
        getCurrentSession().saveOrUpdate(customer);
    }
    
    @Override
    public void delete(Long id) {
        Customer customer = getCurrentSession().get(Customer.class, id);
        if (customer != null) {
            getCurrentSession().delete(customer);
        }
    }
    
    @Override
    public List<Customer> findByCustomerName(String custName) {
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custName = :name", Customer.class)
            .setParameter("name", custName);
        return query.list();
    }
    
    @Override
    public Customer findByCustomerEmail(String custEmail) {
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custEmail = :email", Customer.class)
            .setParameter("email", custEmail);
        return query.uniqueResult();
    }
    
        
    @Override
    public Customer findByUsername(String username) {
    	System.out.println("查詢使用者：" + username); // 加入除錯訊息
        
    	Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custUsername = :username", Customer.class)
            .setParameter("username", username);
        
    	Customer customer = query.uniqueResult();
        
        System.out.println("查詢結果：" + (customer != null ? "找到" : "未找到"));  // 加入除錯訊息
        
        return query.uniqueResult();
    }
    
    @Override
    public Customer findByEmail(String email) {
        Query<Customer> query = getCurrentSession()
            .createQuery("FROM Customer WHERE custEmail = :email", Customer.class)
            .setParameter("email", email);
        return query.uniqueResult();
    }
}