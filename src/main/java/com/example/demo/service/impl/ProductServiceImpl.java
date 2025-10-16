package com.example.demo.service.impl;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductDAO productDAO;
    
    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new RuntimeException("找不到產品，ID: " + id);
        }
        return product;
    }
    
    @Override
    public void saveProduct(Product product) {
        productDAO.save(product);
    }
    
    @Override
    public void updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productDAO.findById(id);
        if (existingProduct == null) {
            throw new RuntimeException("找不到產品，ID: " + id);
        }
        
        existingProduct.setProdName(updatedProduct.getProdName());
        existingProduct.setProdType(updatedProduct.getProdType());
        existingProduct.setProdPrice(updatedProduct.getProdPrice());
        existingProduct.setProdLine(updatedProduct.getProdLine());
        
        productDAO.save(existingProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new RuntimeException("找不到產品，ID: " + id);
        }
        productDAO.delete(id);
    }
}