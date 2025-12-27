package org.example.service.impl;

import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository = new EmployeeRepositoryImpl();

    @Override
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Override
    public void create(Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank())
            throw new RuntimeException("Tên nhân viên không được để trống");

        repository.save(employee);
    }

    @Override
    public void update(Employee employee) {
        repository.update(employee);
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
