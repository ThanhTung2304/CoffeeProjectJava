package org.example;

import org.example.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối MySQL...");

        String sql = "SELECT * FROM account";

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối thành công tới MySQL!");
                System.out.println("Tên cơ sở dữ liệu: " + conn.getCatalog());

                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);

                System.out.println("\n--- DỮ LIỆU TRONG BẢNG ACCOUNT ---");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    int active = rs.getInt("is_active");


                    System.out.println("ID: " + id +
                            ", Username: " + username +
                            ", Password: " + password +
                            ", Active: " + active);
                }

            } else {
                System.out.println("Kết nối thất bại!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi kết nối MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
