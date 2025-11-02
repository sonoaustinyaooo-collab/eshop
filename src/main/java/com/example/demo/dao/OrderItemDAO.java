package com.example.demo.dao;

import com.example.demo.model.OrderItem;
import java.util.List;

public interface OrderItemDAO {
    List<OrderItem> findAll();
    OrderItem findById(Long id);
    void save(OrderItem orderItem);
    void delete(Long id);
    List<OrderItem> findByOrderId(Long orderId);
}