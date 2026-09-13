package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.repository.StatisticRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticRepositoryImpl implements StatisticRepository {

    @Override
    public int countCustomers() {
        String sql = "SELECT COUNT(*) FROM customer";
        return getCount(sql);
    }

    @Override
    public int countEmployees() {
        String sql = "SELECT COUNT(*) FROM employee";
        return getCount(sql);
    }

    @Override
    public double getMonthlyRevenue() {
        String sql = """
            SELECT IFNULL(SUM(total_amount), 0)
            FROM orders
            WHERE status = 'COMPLETED'
              AND MONTH(createdTime) = MONTH(CURRENT_DATE())
              AND YEAR(createdTime) = YEAR(CURRENT_DATE())
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy doanh thu tháng", e);
        }

        return 0;
    }

    @Override
    public int getTotalInventory() {
        String sql = "SELECT IFNULL(SUM(quantity), 0) FROM inventory";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng tồn kho", e);
        }
        return 0;
    }

    @Override
    public int getTotalExported() {
        String sql = """
            SELECT IFNULL(SUM(ABS(quantity_change)), 0)
            FROM inventory_history
            WHERE action = 'EXPORT'
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng xuất kho", e);
        }
        return 0;
    }

    @Override
    public int getExportedTotal() {
        return getTotalExported();
    }

    @Override
    public int countMonthlyReservations() {
        String sql = "SELECT COUNT(*) FROM reservations";
        return getCount(sql);
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực thi truy vấn: " + sql, e);
        }
        return 0;
    }
}
