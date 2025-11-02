package com.example.demo.dao;

import com.example.demo.model.Customer;
import java.util.List;

public interface CustomerDAO {
    List<Customer> findAll();
    Customer findById(Long id);
    void save(Customer customer);
    void delete(Long id);
    List<Customer> findByCustomerName(String custName);
    Customer findByCustomerEmail(String custEmail);  
    // 根據使用者名稱查詢
    Customer findByUsername(String username); 
    // 根據 Email 查詢
    Customer findByEmail(String email);
}