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

