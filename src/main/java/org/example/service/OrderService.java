package org.example.service;

import org.example.entity.Order;
<<<<<<< HEAD
import java.util.List;

public interface OrderService {

    List<Order> findAll();

    void create(Order order);
    void updateCustomer(int orderId, Integer customerId);


    void deleteById(int id);
=======

import java.util.List;

public interface OrderService {
    void checkout(int customerId, double totalAmount);
    List<Order> getHistoryByCustomer(int customerId);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
}
