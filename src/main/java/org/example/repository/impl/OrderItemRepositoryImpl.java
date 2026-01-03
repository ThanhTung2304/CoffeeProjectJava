package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.OrderItem;
import org.example.repository.OrderItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepositoryImpl implements OrderItemRepository {

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        List<OrderItem> list = new ArrayList<>();

        String sql = "SELECT * FROM order_item WHERE order_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("price")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi load order_item", e);
        }

        return list;
    }


    @Override
    public void save(OrderItem item) {
        String sql = """
            INSERT INTO order_item(order_id, product_name, quantity, price)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getOrderId());
            ps.setString(2, item.getProductName());
            ps.setInt(3, item.getQuantity());
            ps.setInt(4, item.getPrice());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm order_item", e);
        }
    }
    @Override
    public void update(int id, int price, int quantity) {
        String sql = """
        UPDATE order_item
        SET price = ?, quantity = ?
        WHERE id = ?
    """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, price);
            ps.setInt(2, quantity);
            ps.setInt(3, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM order_item WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa order_item", e);
        }
    }

    @Override
    public int calculateTotal(int orderId) {
        String sql = """
            SELECT SUM(quantity * price) 
            FROM order_item 
            WHERE order_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tính tổng tiền", e);
        }

        return 0;
    }

}
