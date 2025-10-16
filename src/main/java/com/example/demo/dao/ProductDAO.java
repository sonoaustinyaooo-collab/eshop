package com.example.demo.dao;

import com.example.demo.model.Product;
import java.util.List;

public interface ProductDAO {
    List<Product> findAll();
    Product findById(Long id);
    void save(Product product);
    void delete(Long id);
}