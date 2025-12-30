package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.EmployeeService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl() {
        this.employeeRepository = new EmployeeRepositoryImpl();
    }

    /* ================= FIND ALL ================= */
    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    /* ================= CREATE ================= */
    @Override
    public void create(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee không được null");
        }

        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }

        if (employee.getAccountId() <= 0) {
            throw new IllegalArgumentException("Employee phải gắn với account hợp lệ");
        }

        employeeRepository.save(employee);
    }

    /* ================= UPDATE ================= */
    @Override
    public void update(Employee employee) {

        if (employee == null || employee.getId() <= 0) {
            throw new IllegalArgumentException("Employee không hợp lệ");
        }

        employeeRepository.update(employee);
    }

    /* ================= DELETE ================= */
    @Override
    public void deleteById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID nhân viên không hợp lệ");
        }

        employeeRepository.deleteById(id);
    }

    @Override
    public void save(Employee emp) {

        String sql = """
        INSERT INTO employee (name, position, account_id)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPosition());
            ps.setInt(3, emp.getAccountId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm nhân viên", e);
        }
    }

}
