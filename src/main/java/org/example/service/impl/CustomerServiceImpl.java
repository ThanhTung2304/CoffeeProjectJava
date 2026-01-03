package org.example.service.impl;

import org.example.entity.Customer;
<<<<<<< HEAD
import org.example.event.DataChangeEventBus;
=======
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

<<<<<<< HEAD
    private final CustomerRepository repository = new CustomerRepositoryImpl();

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
=======
    private final CustomerRepository repo = new CustomerRepositoryImpl();

    @Override
    public List<Customer> findAll() {
        return repo.findAll();
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
    }

    @Override
    public void create(Customer customer) {
<<<<<<< HEAD
        if (customer == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không hợp lệ");
        }
        repository.save(customer);
        DataChangeEventBus.notifyChange();
=======
        if (repo.findByPhone(customer.getPhone()) != null) {
            throw new RuntimeException("SĐT đã tồn tại");
        }
        repo.save(customer);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
    }

    @Override
    public void update(Customer customer) {
<<<<<<< HEAD
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
=======
        repo.update(customer);
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }
}

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
