package org.example.service.impl;

import org.example.entity.Customer;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo = new CustomerRepositoryImpl();

    @Override
    public List<Customer> findAll() {
        return repo.findAll();
    }

    @Override
    public void create(Customer customer) {
        if (repo.findByPhone(customer.getPhone()) != null) {
            throw new RuntimeException("SĐT đã tồn tại");
        }
        repo.save(customer);
    }

    @Override
    public void update(Customer customer) {
        repo.update(customer);
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }
}

