package org.example.service.impl;

import org.example.entity.Order;
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

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

