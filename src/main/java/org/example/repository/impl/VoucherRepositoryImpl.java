package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherRepositoryImpl implements VoucherRepository {

    @Override
    public List<Voucher> findAll(String keyword, String status) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers WHERE (code LIKE ? OR note LIKE ?)";
        if (status != null && !status.equals("Tất cả")) {
            sql += " AND status = ?";
        }
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            if (status != null && !status.equals("Tất cả")) {
                ps.setString(3, status);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher v = new Voucher();
                v.setId(rs.getInt("id"));
                v.setCode(rs.getString("code"));
                v.setDiscountType(rs.getString("discount_type"));
                v.setDiscountValue(rs.getDouble("discount_value"));
                v.setStartDate(rs.getDate("start_date").toLocalDate());
                v.setEndDate(rs.getDate("end_date").toLocalDate());
                v.setStatus(rs.getString("status"));
                v.setUsageLimit((Integer) rs.getObject("usage_limit"));
                v.setUsedCount((Integer) rs.getObject("used_count"));
                v.setNote(rs.getString("note"));
                list.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM vouchers WHERE code=?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher v = new Voucher();
                v.setId(rs.getInt("id"));
                v.setCode(rs.getString("code"));
                v.setDiscountType(rs.getString("discount_type"));
                v.setDiscountValue(rs.getDouble("discount_value"));
                v.setStartDate(rs.getDate("start_date").toLocalDate());
                v.setEndDate(rs.getDate("end_date").toLocalDate());
                v.setStatus(rs.getString("status"));
                v.setUsageLimit((Integer) rs.getObject("usage_limit"));
                v.setUsedCount((Integer) rs.getObject("used_count"));
                v.setNote(rs.getString("note"));
                return v;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void save(Voucher v) {
        String sql = "INSERT INTO vouchers(code, discount_type, discount_value, start_date, end_date, status, usage_limit, used_count, note) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, v.getStatus());
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount(), Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Voucher v) {
        String sql = "UPDATE vouchers SET code=?, discount_type=?, discount_value=?, start_date=?, end_date=?, status=?, usage_limit=?, used_count=?, note=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, v.getStatus());
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount(), Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.setInt(10, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM vouchers WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}