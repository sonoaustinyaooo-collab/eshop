package com.example.demo.service.impl;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerDAO customerDAO;
    
    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + id);
        }
        return customer;
    }
    
    @Override
    public void saveCustomer(Customer customer) {
        // 業務邏輯驗證
        if (customer.getCustName() == null || customer.getCustName().isEmpty()) {
            throw new RuntimeException("客戶名稱不可為空");
        }
        if (customer.getCustEmail() == null || customer.getCustEmail().isEmpty()) {
            throw new RuntimeException("客戶 Email 不可為空");
        }
        customerDAO.save(customer);
    }
    
    @Override
    public void updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = customerDAO.findById(id);
        if (existingCustomer == null) {
            throw new RuntimeException("找不到客戶，ID: " + id);
        }
        
        existingCustomer.setCustName(updatedCustomer.getCustName());
        existingCustomer.setCustEmail(updatedCustomer.getCustEmail());
        existingCustomer.setCustPhone(updatedCustomer.getCustPhone());
        existingCustomer.setCustAddress(updatedCustomer.getCustAddress());
        
        customerDAO.save(existingCustomer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            throw new RuntimeException("找不到客戶，ID: " + id);
        }
        customerDAO.delete(id);
    }
    @Override
    public List<Customer> getCustomersByName(String custName) {
        return customerDAO.findByCustomerName(custName);
    }
    
    @Override
    public Customer getCustomerByEmail(String custEmail) {
        return customerDAO.findByCustomerEmail(custEmail);
    }
}