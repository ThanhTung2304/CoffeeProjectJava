package org.example.repository;

import org.example.entity.Order;
import java.util.List;

public interface OrderRepository {
<<<<<<< HEAD

    List<Order> findAll();

    Order findById(int id);

    void save(Order order);
    void updateCustomer(int orderId, Integer customerId);


    void deleteById(int id);
=======
    void save(Order order);
    List<Order> findByCustomerId(int customerId);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
}
