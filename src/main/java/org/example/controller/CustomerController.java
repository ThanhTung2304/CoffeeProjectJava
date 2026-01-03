package org.example.controller;

import org.example.entity.Customer;
import org.example.service.CustomerService;
import org.example.service.impl.CustomerServiceImpl;

import java.util.List;

public class CustomerController {

    private final CustomerService service = new CustomerServiceImpl();

    public List<Customer> getAll() {
        return service.findAll();
    }

    public void create(String name, String phone, String email) {
        service.create(new Customer(name, phone, email));
    }

    public void update(Customer customer) {
        service.update(customer);
    }

    public void delete(int id) {
        service.deleteById(id);
    }

    public void addPoints(int customerId, int total) {
        service.addPoints(customerId, total);
    }

    public void usePoints(int customerId, int points) {
        service.usePoints(customerId, points);
    }
}
