package org.example.repository;

import org.example.entity.Order;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    List<Order> findByCustomerId(int customerId);
}
