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

        String sql = """
    SELECT 
        e.id,
        e.name,
        e.phone,
        e.position,
         e.account_id,
        a.username,
        e.createdTime,
        e.updateTime
    FROM employee e
    LEFT JOIN account a ON e.account_id = a.id
""";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee e = mapRow(rs);

                // 👉 GẮN TẠM username VÀO position để hiển thị (KHÔNG sửa entity)
                String username = rs.getString("username");
                e.setUsername(rs.getString("username"));


                list.add(e);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi lấy danh sách employee", ex);
        }

        return list;
    }

    @Override
    public Employee findById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Employee emp) {
        String sql = """
            INSERT INTO employee (name, phone, position, account_id)
            VALUES (?, ?, ?, ?)
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

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu employee", e);
        }
    }

    @Override
    public void update(Employee emp) {
        String sql = """
            UPDATE employee
            SET name=?, phone=?, position=?, account_id=?, updateTime=NOW()
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

            ps.setInt(5, emp.getId());
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

    public void insert(Employee emp) {
        String sql = """
        INSERT INTO employee(name, phone, position, account_id)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPhone());
            ps.setString(3, emp.getPosition());
            ps.setInt(4, emp.getAccountId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi");
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
