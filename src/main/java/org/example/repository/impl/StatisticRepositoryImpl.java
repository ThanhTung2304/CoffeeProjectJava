package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.repository.StatisticRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatisticRepositoryImpl implements StatisticRepository {

        private Connection conn = DatabaseConfig.getConnection();

        @Override
        public int countCustomers() {
            String sql = "SELECT COUNT(*) FROM account WHERE role = 'CUSTOMER'";
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
            SELECT COALESCE(SUM(total_amount), 0)
            FROM orders
            WHERE MONTH(created_time) = MONTH(CURRENT_DATE())
              AND YEAR(created_time) = YEAR(CURRENT_DATE())
        """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public int countMonthlyReservations() {
            String sql = """
            SELECT COUNT(*)
            FROM reservations
            WHERE MONTH(time) = MONTH(CURRENT_DATE())
              AND YEAR(time) = YEAR(CURRENT_DATE())
        """;
            return getCount(sql);
        }

        @Override
        public String getBestSellingProduct() {
            String sql = """
            SELECT p.name
            FROM order_detail od
            JOIN product p ON od.product_id = p.id
            GROUP BY p.name
            ORDER BY SUM(od.quantity) DESC
            LIMIT 1
        """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("name");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Chưa có";
        }

        private int getCount(String sql) {
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) return rs.getInt(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }


