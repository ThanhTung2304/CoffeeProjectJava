package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.repository.StatisticRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            SELECT IFNULL(SUM(ABS(h.quantity_change) * p.price), 0)
            FROM inventory_history h
            JOIN product p ON h.product_id = p.id
            WHERE h.action = 'EXPORT'
              AND MONTH(h.createdTime) = MONTH(CURRENT_DATE())
              AND YEAR(h.createdTime) = YEAR(CURRENT_DATE())
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi thống kê doanh thu", e);
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
        } catch (Exception e) {
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }



    @Override
    public int getExportedTotal() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public int countMonthlyReservations() {
        return getCount("""
            SELECT COUNT(*)
            FROM reservations
            WHERE MONTH(time) = MONTH(CURRENT_DATE())
              AND YEAR(time) = YEAR(CURRENT_DATE())
        """);
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi thống kê", e);
        }
        return 0;
    }
}
