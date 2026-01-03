package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Order;
import org.example.repository.OrderRepository;

<<<<<<< HEAD
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();

        String sql = """
    SELECT o.id,
           o.customer_id,
           o.total_amount,
           o.createdTime,
           c.name AS customer_name
    FROM orders o
    LEFT JOIN customer c ON o.customer_id = c.id
""";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách order", e);
        }
        return list;
    }

    @Override
    public Order findById(int id) {
        String sql = """
            SELECT o.*, c.name AS customer_name
            FROM orders o
            LEFT JOIN customer c ON o.customer_id = c.id
            WHERE o.id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm order", e);
        }
        return null;
    }

    @Override
    public void save(Order o) {
        String sql = """
            INSERT INTO orders (customer_id, total_amount)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (o.getCustomerId() == null)
                ps.setNull(1, Types.INTEGER);
            else
                ps.setInt(1, o.getCustomerId());

            ps.setInt(2, o.getTotalAmount());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu order", e);
        }
    }
    public void updateCustomer(int orderId, Integer customerId) {
        String sql = "UPDATE orders SET customer_id = ? WHERE id = ?";
=======
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders(customer_id, total_amount, created_time) VALUES (?,?,?)";

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

<<<<<<< HEAD
            if (customerId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, customerId);
            }

            ps.setInt(2, orderId);
            ps.executeUpdate();
=======
            ps.setInt(1, order.getCustomerId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setTimestamp(3, Timestamp.valueOf(order.getCreatedTime()));
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
<<<<<<< HEAD


    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa order", e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));

        int customerId = rs.getInt("customer_id");
        if (rs.wasNull()) {
            order.setCustomerId(null);
        } else {
            order.setCustomerId(customerId);
        }

        order.setTotalAmount(rs.getInt("total_amount"));
        order.setCreatedTime(
                rs.getTimestamp("createdTime").toLocalDateTime()
        );
        order.setCustomerName(rs.getString("customer_name"));

        return order; //
    }
=======
    @Override
    public List<Order> findByCustomerId(int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_time DESC";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getInt("id"));
                    o.setCustomerId(rs.getInt("customer_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setCreatedTime(
                            rs.getTimestamp("created_time").toLocalDateTime()
                    );
                    list.add(o);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy lịch sử đơn hàng", e);
        }
        return list;
    }

>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

}
