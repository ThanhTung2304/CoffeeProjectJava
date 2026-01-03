package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Customer;
import org.example.repository.CustomerRepository;

import java.sql.*;
<<<<<<< HEAD
=======
import java.time.LocalDateTime;
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
<<<<<<< HEAD

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
=======
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
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        }
        return list;
    }

    @Override
<<<<<<< HEAD
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
=======
    public void save(Customer c) {
        String sql = """
            INSERT INTO customers(name, phone, email, total_point)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
<<<<<<< HEAD
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu customer", e);
=======
            ps.setInt(4, c.getTotalPoint());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm khách hàng", e);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        }
    }

    @Override
    public void update(Customer c) {
        String sql = """
<<<<<<< HEAD
            UPDATE customer
            SET name = ?, phone = ?, email = ?, updateTime = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
=======
            UPDATE customers
            SET name=?, phone=?, email=?, total_point=?
            WHERE id=?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
<<<<<<< HEAD
            ps.setInt(4, c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật customer", e);
=======
            ps.setInt(4, c.getTotalPoint());
            ps.setInt(5, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật khách hàng", e);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        }
    }

    @Override
    public void deleteById(int id) {
<<<<<<< HEAD
        String sql = "DELETE FROM customer WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
=======
        String sql = "DELETE FROM customers WHERE id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< HEAD
            throw new RuntimeException("Lỗi xóa customer", e);
=======
            throw new RuntimeException("Lỗi xóa khách hàng", e);
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        }
    }

    @Override
<<<<<<< HEAD
    public void updatePoints(int customerId, int newPoints) {
        String sql = """
            UPDATE customer
            SET points = ?, updateTime = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newPoints);
=======
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
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
            ps.setInt(2, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< HEAD
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
=======
            throw new RuntimeException("Lỗi cộng điểm khách hàng", e);
        }
    }

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
}
