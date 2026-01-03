package org.example.service;

import org.example.entity.Customer;
import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    void create(Customer customer);

    void update(Customer customer);

    void deleteById(int id);

    void addPoints(int customerId, int orderTotal);

    void usePoints(int customerId, int points);
}
