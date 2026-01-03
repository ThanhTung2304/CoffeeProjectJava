package org.example.repository;

import org.example.entity.OrderItem;
import java.util.List;

public interface OrderItemRepository {

    List<OrderItem> findByOrderId(int orderId);

    void save(OrderItem item);

    void update(int id, int price, int quantity);


    void deleteById(int id);

    int calculateTotal(int orderId);
}
