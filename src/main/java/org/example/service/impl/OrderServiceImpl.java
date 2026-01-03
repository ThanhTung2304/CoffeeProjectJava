package org.example.service.impl;

import org.example.entity.Order;
<<<<<<< HEAD
import org.example.event.DataChangeEventBus;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.CustomerService;
=======
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.repository.impl.OrderRepositoryImpl;
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
import org.example.service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

<<<<<<< HEAD
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
=======
    private OrderRepository orderRepo = new OrderRepositoryImpl();
    private CustomerRepository customerRepo = new CustomerRepositoryImpl();

    @Override
    public void checkout(int customerId, double totalAmount) {

        // 1. Lưu hóa đơn
        orderRepo.save(new Order(customerId, totalAmount));

        // 2. Tính điểm
        int point = (int)(totalAmount / 10000);

        // 3. Cộng điểm khách hàng
        customerRepo.addPoint(customerId, point);
    }
    @Override
    public List<Order> getHistoryByCustomer(int customerId) {
        return orderRepo.findByCustomerId(customerId);
    }

}

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
