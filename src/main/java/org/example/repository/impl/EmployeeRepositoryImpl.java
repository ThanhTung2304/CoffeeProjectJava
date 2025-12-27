package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Override
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách employee", e);
        }
        return list;
    }

    @Override
    public Employee findById(int id) {
        String sql = "SELECT * FROM employee WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm employee", e);
        }
        return null;
    }

    @Override
    public void save(Employee emp) {
        String sql = """
            INSERT INTO employee (name, phone, position, account_id, createdTime)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPhone());
            ps.setString(3, emp.getPosition());

            if (emp.getAccountId() != null)
                ps.setInt(4, emp.getAccountId());
            else
                ps.setNull(4, Types.INTEGER);

            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) emp.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu employee", e);
        }
    }

    @Override
    public void update(Employee emp) {
        String sql = """
            UPDATE employee
            SET name=?, phone=?, position=?, account_id=?, updateTime=?
            WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPhone());
            ps.setString(3, emp.getPosition());

            if (emp.getAccountId() != null)
                ps.setInt(4, emp.getAccountId());
            else
                ps.setNull(4, Types.INTEGER);

            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, emp.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật employee", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM employee WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa employee", e);
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setPhone(rs.getString("phone"));
        e.setPosition(rs.getString("position"));

        int accId = rs.getInt("account_id");
        e.setAccountId(rs.wasNull() ? null : accId);

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updateTime");

        if (created != null) e.setCreatedTime(created.toLocalDateTime());
        if (updated != null) e.setUpdateTime(updated.toLocalDateTime());

        return e;
    }
}
