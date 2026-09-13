package org.example.service.impl;

import org.example.entity.Customer;
import org.example.event.DataChangeEventBus;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl() {
        this.customerRepository = new CustomerRepositoryImpl();
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public void create(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer không được null");
        }

        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }

        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        customerRepository.save(customer);

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void update(Customer customer) {

        if (customer == null || customer.getId() <= 0) {
            throw new IllegalArgumentException("Customer không hợp lệ");
        }

        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }

        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        customerRepository.update(customer);

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void deleteById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID khách hàng không hợp lệ");
        }

        customerRepository.delete(id);

        DataChangeEventBus.notifyChange();
    }

    @Override
    public Customer findById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        return customerRepository.findById(id);
    }

    @Override
    public List<Customer> search(String keyword, String status) {
        return customerRepository.search(keyword, status);
    }
}
