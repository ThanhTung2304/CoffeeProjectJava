package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Reservation;
import org.example.repository.ReservationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepository {

    @Override
    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservations (customer_id, customer_name, table_number, time, status, note) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (reservation.getCustomerId() != null) {
                ps.setInt(1, reservation.getCustomerId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, reservation.getCustomerName());
            ps.setInt(3, reservation.getTableNumber());
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getTime()));
            ps.setString(5, reservation.getStatus());
            ps.setString(6, reservation.getNote());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu reservation", e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET customer_id=?, customer_name=?, table_number=?, time=?, status=?, note=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (reservation.getCustomerId() != null) {
                ps.setInt(1, reservation.getCustomerId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, reservation.getCustomerName());
            ps.setInt(3, reservation.getTableNumber());
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getTime()));
            ps.setString(5, reservation.getStatus());
            ps.setString(6, reservation.getNote());
            ps.setInt(7, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật reservation", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa reservation", e);
        }
    }

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm reservation", e);
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách reservation", e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByTableNumber(int tableNumber) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE table_number=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm reservation theo bàn", e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByCustomerName(String customerName) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_name=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm reservation theo khách hàng", e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm reservation theo trạng thái", e);
        }
        return list;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getInt("table_number"),
                rs.getTimestamp("time").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("note")
        );
        int custId = rs.getInt("customer_id");
        r.setCustomerId(rs.wasNull() ? null : custId);
        return r;
    }
}
