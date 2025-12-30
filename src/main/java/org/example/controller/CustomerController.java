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

    public void add(String name, String phone, String email) {
        service.create(new Customer(name, phone, email));
    }

    public void update(Customer c) {
        service.update(c);
    }

    public void delete(int id) {
        service.delete(id);
    }
}

