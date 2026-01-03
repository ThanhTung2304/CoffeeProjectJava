package org.example.controller;

import org.example.entity.OrderItem;
import org.example.service.OrderItemService;
import org.example.service.impl.OrderItemServiceImpl;

import java.util.List;

public class OrderItemController {

    private final OrderItemService service =
            new OrderItemServiceImpl();

    public List<OrderItem> getByOrder(int orderId) {
        return service.findByOrderId(orderId);
    }

    public void addItem(int orderId, String name, int qty, int price) {
        OrderItem item = new OrderItem(name, qty, price);
        item.setOrderId(orderId);
        service.addItem(item);
    }

    public void updateItem(int itemId, int price, int quantity) {
        service.updateItem(itemId, price, quantity);
    }

    public void deleteItem(int id) {
        service.deleteItem(id);
    }

    public int calculateTotal(int orderId) {
        return service.calculateTotal(orderId);
    }
}
