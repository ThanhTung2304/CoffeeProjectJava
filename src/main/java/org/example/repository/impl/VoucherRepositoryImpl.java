package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherRepositoryImpl implements VoucherRepository {

    /**
     * Chuẩn hóa status từ Java/UI -> ENUM trong DB (tiếng Việt)
     */
    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Còn hiệu lực";
        }

        status = status.trim().toUpperCase();

        switch (status) {
            case "CÒN HIỆU LỰC":
            case "CON_HIEU_LUC":
            case "ACTIVE":
                return "Còn hiệu lực";

            case "HẾT HẠN":
            case "HET_HAN":
            case "EXPIRED":
                return "Hết hạn";

            case "ĐÃ SỬ DỤNG":
            case "DA_SU_DUNG":
            case "USED":
                return "Đã sử dụng";

            default:
                System.err.println("⚠ Status không hợp lệ: " + status + " → mặc định Còn hiệu lực");
                return "Còn hiệu lực";
        }
    }

    @Override
    public List<Voucher> findAll(String keyword, String status) {
        List<Voucher> list = new ArrayList<>();

        String sql = "SELECT * FROM vouchers WHERE (code LIKE ? OR note LIKE ?)";
        if (status != null && !status.equalsIgnoreCase("Tất cả")) {
            sql += " AND status = ?";
        }

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            if (status != null && !status.equalsIgnoreCase("Tất cả")) {
                ps.setString(3, normalizeStatus(status));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher v = mapResultSet(rs);
                list.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        String sql = "SELECT * FROM vouchers WHERE code=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void save(Voucher v) {
        String sql = """
                INSERT INTO vouchers
                (code, discount_type, discount_value, start_date, end_date, status, usage_limit, used_count, note)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, normalizeStatus(v.getStatus()));
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
        String sql = """
                UPDATE vouchers SET
                code=?,
                discount_type=?,
                discount_value=?,
                start_date=?,
                end_date=?,
                status=?,
                usage_limit=?,
                used_count=?,
                note=?
                WHERE id=?
                """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM vouchers WHERE id=?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Map ResultSet -> Voucher (tách riêng cho gọn & sạch)
     */
    private Voucher mapResultSet(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setId(rs.getInt("id"));
        v.setCode(rs.getString("code"));
        v.setDiscountType(rs.getString("discount_type"));
        v.setDiscountValue(rs.getDouble("discount_value"));
        v.setStartDate(rs.getDate("start_date").toLocalDate());
        v.setEndDate(rs.getDate("end_date").toLocalDate());
        v.setStatus(rs.getString("status")); // DB trả về tiếng Việt
        v.setUsageLimit((Integer) rs.getObject("usage_limit"));
        v.setUsedCount((Integer) rs.getObject("used_count"));
        v.setNote(rs.getString("note"));
        return v;
    }
}
