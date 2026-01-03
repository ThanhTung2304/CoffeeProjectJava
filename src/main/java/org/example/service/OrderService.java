package org.example.service;

import org.example.entity.Order;
import java.util.List;

public interface OrderService {

    List<Order> findAll();

    void create(Order order);
    void updateCustomer(int orderId, Integer customerId);


    void deleteById(int id);
}
