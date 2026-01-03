package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Customer;
import org.example.repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();

        String sql = """
            SELECT id, name, phone, email, points, createdTime, updateTime
            FROM customer
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách customer", e);
        }
        return list;
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customer WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm customer", e);
        }
        return null;
    }

    @Override
    public void save(Customer c) {
        String sql = """
            INSERT INTO customer (name, phone, email)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu customer", e);
        }
    }

    @Override
    public void update(Customer c) {
        String sql = """
            UPDATE customer
            SET name = ?, phone = ?, email = ?, updateTime = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setInt(4, c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật customer", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa customer", e);
        }
    }

    @Override
    public void updatePoints(int customerId, int newPoints) {
        String sql = """
            UPDATE customer
            SET points = ?, updateTime = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newPoints);
            ps.setInt(2, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật điểm", e);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();

        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setPoints(rs.getInt("points"));

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updateTime");

        if (created != null) c.setCreatedTime(created.toLocalDateTime());
        if (updated != null) c.setUpdateTime(updated.toLocalDateTime());

        return c;
    }
}
