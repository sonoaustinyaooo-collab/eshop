package com.example.demo.service;

import com.example.demo.model.Customer;
import java.util.List;

public interface CustomerService {
    
    List<Customer> getAllCustomers();
    
    Customer getCustomerById(Long id);
    
    void saveCustomer(Customer customer);
    
    void updateCustomer(Long id, Customer updatedCustomer);
    
    void deleteCustomer(Long id);
}