package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.TableSeat;
import org.example.repository.TableRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableRepositoryImpl implements TableRepository {

    @Override
    public List<TableSeat> findAll() {
        List<TableSeat> list = new ArrayList<>();
        String sql = "SELECT table_number, name, capacity, status, note FROM tables";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TableSeat t = new TableSeat();
                t.setTableNumber(rs.getInt("table_number"));
                t.setName(rs.getString("name"));
                t.setCapacity(rs.getInt("capacity"));
                t.setStatus(rs.getString("status"));
                t.setNote(rs.getString("note"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void add(TableSeat table) {
        String sql = "INSERT INTO tables(name, capacity, status, note) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table.getName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setString(4, table.getNote());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TableSeat table) {
        String sql = "UPDATE tables SET name=?, capacity=?, status=?, note=? WHERE table_number=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table.getName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setString(4, table.getNote());
            ps.setInt(5, table.getTableNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int tableNumber) {
        String sql = "DELETE FROM tables WHERE table_number=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}