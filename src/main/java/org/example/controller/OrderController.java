package org.example.controller;

import org.example.entity.Order;
import org.example.service.OrderService;
import org.example.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderController {

    private final OrderService service = new OrderServiceImpl();

    public List<Order> getAll() {
        return service.findAll();
    }

    public void create(Integer customerId, int totalAmount) {
        service.create(new Order(customerId, totalAmount));
    }

    public void updateCustomer(int orderId, Integer customerId) {
        service.updateCustomer(orderId, customerId);
    }


    public void delete(int id) {
        service.deleteById(id);
    }
}
