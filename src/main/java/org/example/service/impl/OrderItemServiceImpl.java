package org.example.service.impl;

import org.example.entity.OrderItem;
import org.example.repository.OrderItemRepository;
import org.example.repository.impl.OrderItemRepositoryImpl;
import org.example.service.OrderItemService;

public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository repository =
            new OrderItemRepositoryImpl();

    @Override
    public java.util.List<OrderItem> findByOrderId(int orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public void addItem(OrderItem item) {
        repository.save(item);
    }

    @Override
    public void updateItem(int itemId, int price, int quantity) {
        repository.update(itemId, price, quantity);
    }


    @Override
    public void deleteItem(int id) {
        repository.deleteById(id);
    }

    @Override
    public int calculateTotal(int orderId) {
        return repository.calculateTotal(orderId);
    }
}
