package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Reservation;
import org.example.event.DataChangeEventBus;
import org.example.repository.ReservationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepository {

    private final Connection connection;

    // Constructor mặc định: dùng DatabaseConfig
    public ReservationRepositoryImpl() {
        this.connection = DatabaseConfig.getConnection();
    }

    // Constructor có tham số (nếu muốn truyền connection từ ngoài)
    public ReservationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservations (customer_name, table_number, time, status, note) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reservation.getCustomerName());
            ps.setInt(2, reservation.getTableNumber());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getTime()));
            ps.setString(4, reservation.getStatus());
            ps.setString(5, reservation.getNote());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET customer_name=?, table_number=?, time=?, status=?, note=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reservation.getCustomerName());
            ps.setInt(2, reservation.getTableNumber());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getTime()));
            ps.setString(4, reservation.getStatus());
            ps.setString(5, reservation.getNote());
            ps.setInt(6, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        DataChangeEventBus.notifyChange();
    }

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Reservation> findByTableNumber(int tableNumber) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE table_number=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Reservation> findByCustomerName(String customerName) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_name=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getInt("table_number"),
                rs.getTimestamp("time").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("note")
        );
    }
}