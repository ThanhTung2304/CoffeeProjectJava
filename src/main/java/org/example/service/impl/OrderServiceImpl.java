package org.example.service.impl;

import org.example.entity.Order;
import org.example.event.DataChangeEventBus;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.CustomerService;
import org.example.service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository = new OrderRepositoryImpl();
    private final CustomerService customerService = new CustomerServiceImpl();

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public void create(Order order) {

        // tạo đơn cho phép total = 0
        repository.save(order);

        if (order.getCustomerId() != null) {
            customerService.addPoints(
                    order.getCustomerId(),
                    order.getTotalAmount()
            );
        }

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void updateCustomer(int orderId, Integer customerId) {
        repository.updateCustomer(orderId, customerId);
        DataChangeEventBus.notifyChange();
    }


    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
        DataChangeEventBus.notifyChange();
    }
}
