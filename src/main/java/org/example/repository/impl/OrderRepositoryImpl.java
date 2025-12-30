package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Order;
import org.example.repository.OrderRepository;

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


        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, order.getCustomerId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setTimestamp(3, Timestamp.valueOf(order.getCreatedTime()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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


}
