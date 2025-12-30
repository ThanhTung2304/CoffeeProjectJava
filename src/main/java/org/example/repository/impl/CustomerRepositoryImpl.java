package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Customer;
import org.example.repository.CustomerRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setTotalPoint(rs.getInt("total_point"));

                Timestamp created = rs.getTimestamp("created_time");
                if (created != null)
                    c.setCreatedTime(created.toLocalDateTime());

                Timestamp updated = rs.getTimestamp("update_time");
                if (updated != null)
                    c.setUpdateTime(updated.toLocalDateTime());

                list.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy khách hàng", e);
        }
        return list;
    }

    @Override
    public void save(Customer c) {
        String sql = """
            INSERT INTO customers(name, phone, email, total_point)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setInt(4, c.getTotalPoint());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm khách hàng", e);
        }
    }

    @Override
    public void update(Customer c) {
        String sql = """
            UPDATE customers
            SET name=?, phone=?, email=?, total_point=?
            WHERE id=?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setInt(4, c.getTotalPoint());
            ps.setInt(5, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật khách hàng", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM customers WHERE id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa khách hàng", e);
        }
    }

    @Override
    public Customer findByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    c.setEmail(rs.getString("email"));
                    c.setTotalPoint(rs.getInt("total_point"));
                    return c;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm khách hàng", e);
        }
        return null;
    }

    @Override
    public void addPoint(int customerId, int point) {
        String sql = """
        UPDATE customers
        SET total_point = total_point + ?
        WHERE id = ?
    """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, point);
            ps.setInt(2, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cộng điểm khách hàng", e);
        }
    }

}
