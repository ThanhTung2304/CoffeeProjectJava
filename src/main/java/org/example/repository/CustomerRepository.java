package org.example.repository;

import org.example.entity.Customer;
import java.util.List;

public interface CustomerRepository {

    List<Customer> findAll();

    Customer findById(int id);

    void save(Customer customer);

    void update(Customer customer);

    void deleteById(int id);

    void updatePoints(int customerId, int newPoints);
}
