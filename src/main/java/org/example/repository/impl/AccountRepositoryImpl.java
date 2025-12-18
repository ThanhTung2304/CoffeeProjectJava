//package org.example.repository.impl;

//import org.example.config.DatabaseConfig;
//import org.example.entity.Account;
//import org.example.repository.AccountRepository;
//
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class AccountRepositoryImpl implements AccountRepository {
//
//
//    /**
//     * Lấy tất cả tài khoản.
//     *
//     * @return danh sách tài khoản
//     */
//    @Override
//    public List<Account> findAll() {
//        List<Account> accounts = new ArrayList<>();
//
//        String sql = "SELECT * FROM account";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                accounts.add(mapRow(rs));
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi lấy danh sách account", e);
//        }
//        return accounts;
//    }
//
//    /**
//     * Tìm tài khoản theo tên đăng nhập.
//     *
//     * @param username tên đăng nhập
//     * @return tài khoản hoặc null nếu không tìm thấy
//     */
//    @Override
//    public Account findByUsername(String username) {
//        String sql = "SELECT * FROM account WHERE username = ?";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, username);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapRow(rs);
//                }
//                return null;
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi tìm account", e);
//        }
//    }
//
//
//    /**
//     * Lưu tài khoản mới.
//     * @param account tài khoản cần lưu
//     */
//    @Override
//    public void save(Account account) {
//        String sql = "INSERT INTO account (username, password, createdTime,updateTime, is_active, role) VALUES (?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, account.getUsername());
//            ps.setString(2, account.getPassword());
//
//            // Coffee project của bạn dùng LocalDateTime → phải convert sang Timestamp
//            if (account.getCreatedTime() != null) {
//                ps.setTimestamp(3, Timestamp.valueOf(account.getCreatedTime()));
//            } else {
//                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
//            }
//
//            // updatedTime có thể null
//            if (account.getUpdateTime() != null) {
//                ps.setTimestamp(4, Timestamp.valueOf(account.getUpdateTime()));
//            } else {
//                ps.setNull(4, java.sql.Types.TIMESTAMP);
//            }
//
//            ps.setBoolean(5, account.isActive());
//
//            // Lấy ID tự động tăng (nếu có)
//            try (ResultSet rs = ps.getGeneratedKeys()) {
//                if (rs.next()) {
//                    account.setId(rs.getInt(1));
//                }
//            }
//
//            ps.setString(6, account.getRole());
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi lưu account", e);
//        }
//    }
//
//
//    /**
//     * Cập nhật tài khoản.
//     *
//     * @param account tài khoản cần cập nhật
//     */
//    @Override
//    public void updated(Account account) {
//
//        String sql = "UPDATE account SET password=?, createdTime=?, updateTime=?, is_active=?, role=? WHERE id=?";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, account.getUsername());
//            ps.setString(2, account.getPassword());
//
//            // Coffee project của bạn dùng LocalDateTime → phải convert sang Timestamp
//            if (account.getCreatedTime() != null) {
//                ps.setTimestamp(3, Timestamp.valueOf(account.getCreatedTime()));
//            } else {
//                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
//            }
//
//            // updatedTime có thể null
//            if (account.getUpdateTime() != null) {
//                ps.setTimestamp(4, Timestamp.valueOf(account.getUpdateTime()));
//            } else {
//                ps.setNull(4, java.sql.Types.TIMESTAMP);
//            }
//
//
//            ps.setBoolean(5, account.isActive());
//
//            ps.setString(6, account.getRole());
//
//            // Lấy ID tự động tăng (nếu có)
//            try (ResultSet rs = ps.getGeneratedKeys()) {
//                if (rs.next()) {
//                    account.setId(rs.getInt(1));
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi lưu account", e);
//        }
//    }
//
//
//    /**
//     * Xóa tài khoản theo ID.
//     *
//     * @param id ID của tài khoản cần xóa
//     */
//    @Override
//    public void deleteById(int id) {
//        String sql = "DELETE FROM account WHERE id = ?";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, id);
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi xóa account", e);
//        }
//    }
//
//
//    /**
//     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
//     * @param username tên đăng nhập
//     * @return true nếu tồn tại, false nếu không
//     */
//    @Override
//    public boolean existsByUsername(String username) {
//        String sql = "SELECT 1 FROM account WHERE username = ?";
//
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, username);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                return rs.next();
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi kiểm tra username", e);
//        }
//    }
//
//    private Account mapRow(ResultSet rs) throws SQLException {
//        Account acc = new Account();
//
//        acc.setId(rs.getInt("id"));
//        acc.setUsername(rs.getString("username"));
//        acc.setPassword(rs.getString("password"));
//
//        // Chuyển Timestamp → LocalDateTime
//        Timestamp created = rs.getTimestamp("createdTime");
//        Timestamp updated = rs.getTimestamp("updateTime");
//
//        if (created != null)
//            acc.setCreatedTime(created.toLocalDateTime());
//
//        if (updated != null)
//            acc.setUpdateTime(updated.toLocalDateTime());
//
//        acc.setRole(rs.getString("role"));
//
//        acc.setActive(rs.getInt("is_active") == 1);
//
//        return acc;
//    }
//
//}
package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import org.example.repository.AccountRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountRepositoryImpl implements AccountRepository {

    /* ================== FIND ALL ================== */
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                accounts.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách account", e);
        }
        return accounts;
    }

    /* ================== FIND BY USERNAME ================== */
    @Override
    public Account findByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm account", e);
        }
        return null;
    }

    /* ================== SAVE (REGISTER) ================== */
    @Override
    public void save(Account account) {
        String sql = """
            INSERT INTO account (username, password, createdTime, updateTime, is_active, role)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            ps.setTimestamp(3, Timestamp.valueOf(
                    account.getCreatedTime() != null
                            ? account.getCreatedTime()
                            : LocalDateTime.now()
            ));

            ps.setNull(4, Types.TIMESTAMP); // updateTime
            ps.setBoolean(5, true);         // is_active
            ps.setString(6, account.getRole());

            ps.executeUpdate(); // 🔥 BẮT BUỘC

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu account", e);
        }
    }

    /* ================== UPDATE ================== */
    @Override
    public void updated(Account account) {
        String sql = """
            UPDATE account
            SET password = ?, updateTime = ?, is_active = ?, role = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getPassword());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(3, account.isActive());
            ps.setString(4, account.getRole());
            ps.setInt(5, account.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật account", e);
        }
    }

    /* ================== DELETE ================== */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM account WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa account", e);
        }
    }

    /* ================== EXISTS BY USERNAME ================== */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM account WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true nếu tồn tại
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra username", e);
        }
    }

    /* ================== MAP RESULT ================== */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account acc = new Account();

        acc.setId(rs.getInt("id"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updateTime");

        if (created != null)
            acc.setCreatedTime(created.toLocalDateTime());

        if (updated != null)
            acc.setUpdateTime(updated.toLocalDateTime());

        acc.setActive(rs.getBoolean("is_active"));
        acc.setRole(rs.getString("role"));

        return acc;
    }
}


