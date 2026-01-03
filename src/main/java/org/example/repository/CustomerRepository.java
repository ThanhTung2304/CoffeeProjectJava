<<<<<<< HEAD
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
=======
    package org.example.repository;

    import org.example.entity.Customer;
    import java.util.List;

    public interface CustomerRepository {

        List<Customer> findAll();

        void save(Customer customer);

        void update(Customer customer);

        void deleteById(int id);

        Customer findByPhone(String phone);

        void addPoint(int customerId, int point);

    }

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
