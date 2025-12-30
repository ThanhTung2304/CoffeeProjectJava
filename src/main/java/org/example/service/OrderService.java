package org.example.service;

import org.example.entity.Order;

import java.util.List;

public interface OrderService {
    void checkout(int customerId, double totalAmount);
    List<Order> getHistoryByCustomer(int customerId);
}
