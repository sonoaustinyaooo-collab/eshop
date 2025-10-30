package com.example.demo.service;

import com.example.demo.model.Customer;
import java.util.List;

public interface CustomerService {
    
    List<Customer> getAllCustomers();
    
    Customer getCustomerById(Long id);
    
    void saveCustomer(Customer customer);
    
    void updateCustomer(Long id, Customer updatedCustomer);
    
    void deleteCustomer(Long id);

	List<Customer> getCustomersByName(String custName); //在客戶管理功能中，按姓名搜尋客戶時使用

	Customer getCustomerByEmail(String custEmail); //在註冊時檢查 Email 是否已被使用
}