package org.example.service.impl;

import org.example.entity.Customer;
import org.example.event.DataChangeEventBus;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository = new CustomerRepositoryImpl();

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public void create(Customer customer) {
        if (customer == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không hợp lệ");
        }
        repository.save(customer);
        DataChangeEventBus.notifyChange();
    }

    @Override
    public void update(Customer customer) {
        repository.update(customer);
        DataChangeEventBus.notifyChange();
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
        DataChangeEventBus.notifyChange();
    }

    // ⭐ 10.000 = 1 điểm
    @Override
    public void addPoints(int customerId, int orderTotal) {
        Customer c = repository.findById(customerId);
        int earned = orderTotal / 10000;
        repository.updatePoints(customerId, c.getPoints() + earned);
        DataChangeEventBus.notifyChange();
    }

    @Override
    public void usePoints(int customerId, int points) {
        Customer c = repository.findById(customerId);
        if (c.getPoints() < points)
            throw new IllegalArgumentException("Không đủ điểm");

        repository.updatePoints(customerId, c.getPoints() - points);
        DataChangeEventBus.notifyChange();
    }
}
