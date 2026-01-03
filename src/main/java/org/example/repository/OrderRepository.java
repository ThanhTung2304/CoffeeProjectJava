package org.example.repository;

import org.example.entity.Order;
import java.util.List;

public interface OrderRepository {

    List<Order> findAll();

    Order findById(int id);

    void save(Order order);
    void updateCustomer(int orderId, Integer customerId);


    void deleteById(int id);
}
