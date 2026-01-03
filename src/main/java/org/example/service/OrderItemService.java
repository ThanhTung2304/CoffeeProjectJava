package org.example.service;

import org.example.entity.OrderItem;
import java.util.List;

public interface OrderItemService {

    List<OrderItem> findByOrderId(int orderId);

    void addItem(OrderItem item);

    void updateItem(int itemId, int price, int quantity);

    void deleteItem(int id);

    int calculateTotal(int orderId);

}
