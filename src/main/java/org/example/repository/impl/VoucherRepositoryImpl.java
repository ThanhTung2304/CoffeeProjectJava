package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherRepositoryImpl implements VoucherRepository {

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) return "Còn hiệu lực";
        String s = status.trim().toUpperCase();
        if (s.contains("HIỆU LỰC") || s.equals("ACTIVE")) return "Còn hiệu lực";
        if (s.contains("HẾT HẠN") || s.equals("EXPIRED")) return "Hết hạn";
        if (s.contains("SỬ DỤNG") || s.equals("USED")) return "Đã sử dụng";
        return "Còn hiệu lực";
    }

    private String normalizeDiscountType(String type) {
        return "Phần trăm";
    }

    @Override
    public List<Voucher> findAll(String keyword, String status) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers WHERE (code LIKE ? OR note LIKE ?)";
        boolean hasStatus = status != null && !status.equalsIgnoreCase("Tất cả");
        if (hasStatus) sql += " AND status = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            if (hasStatus) ps.setString(3, normalizeStatus(status));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm voucher", e);
        }
        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        String sql = "SELECT * FROM vouchers WHERE code = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm voucher theo mã", e);
        }
        return null;
    }

    @Override
    public void save(Voucher v) {
        String sql = "INSERT INTO vouchers (code, discount_type, discount_value, start_date, end_date, status, usage_limit, used_count, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, normalizeDiscountType(v.getDiscountType()));
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, normalizeStatus(v.getStatus()));
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount() != null ? v.getUsedCount() : 0, Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu voucher", e);
        }
    }

    @Override
    public void update(Voucher v) {
        String sql = "UPDATE vouchers SET code=?, discount_type=?, discount_value=?, start_date=?, end_date=?, status=?, usage_limit=?, used_count=?, note=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, normalizeDiscountType(v.getDiscountType()));
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, normalizeStatus(v.getStatus()));
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount(), Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.setInt(10, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật voucher", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa voucher", e);
        }
    }

    @Override
    public void incrementUsedCount(int id) {
        String sql = "UPDATE vouchers SET used_count = used_count + 1, status = CASE "
                + "WHEN usage_limit > 0 AND used_count + 1 >= usage_limit THEN 'Đã sử dụng' "
                + "ELSE status END WHERE id = ? AND (usage_limit IS NULL OR usage_limit = 0 OR used_count < usage_limit)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Voucher không tồn tại hoặc đã đạt giới hạn sử dụng");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tăng số lần sử dụng voucher", e);
        }
    }

    private Voucher mapResultSet(ResultSet rs) throws SQLException {
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
}
