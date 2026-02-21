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
        String sql = "SELECT * FROM customer";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("point"),
                        rs.getInt("status")
                );
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Customer c) {
        String sql = """
            INSERT INTO customer(code,name,phone,email,point,status)
            VALUES (?,?,?,?,?,?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getCode());
            ps.setString(2, c.getName());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getPoint());
            ps.setInt(6, c.getStatus());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Customer c) {
        String sql = """
            UPDATE customer
            SET name=?, phone=?, email=?, status=?
            WHERE id=?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setInt(4, c.getStatus());
            ps.setInt(5, c.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "UPDATE customer SET status=0 WHERE id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customer WHERE id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("point"),
                        rs.getInt("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> search(String keyword, String status) {
        List<Customer> list = new ArrayList<>();

        String sql = """
            SELECT * FROM customer
            WHERE name LIKE ?
            AND (? = '' OR status = ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, status);
            ps.setString(3, status);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("point"),
                        rs.getInt("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
