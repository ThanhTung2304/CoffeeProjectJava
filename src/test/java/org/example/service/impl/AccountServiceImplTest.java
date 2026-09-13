package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;
    private String createdUsername = null;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
    }

    @AfterEach
    void tearDown() {
        if (createdUsername != null) {
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM account WHERE username = ?")) {
                ps.setString(1, createdUsername);
                ps.executeUpdate();
            } catch (SQLException e) {
                // ignore
            }
            createdUsername = null;
        }
    }

    @Test
    @DisplayName("Lấy tất cả danh sách - Phải trả về List không null")
    void testFindAll() {
        List<Account> list = accountService.findAll();
        assertNotNull(list);
    }

    @Test
    @DisplayName("Tìm theo Username hợp lệ")
    void testFindByUsername_Valid() {
        Account acc = accountService.findByUsername("admin");
        assertNotNull(acc, "Account không được null");
        assertEquals("admin", acc.getUsername());
    }

    @Test
    @DisplayName("Thêm mới thất bại - Account bị null")
    void testCreate_NullAccount() {
        assertThrows(IllegalArgumentException.class, () -> accountService.create(null));
    }

    @Test
    @DisplayName("Thêm mới thất bại - Username để trống")
    void testCreate_EmptyUsername() {
        Account acc = new Account("", "123", "USER", true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.create(acc));
        assertEquals("Username không được để trống", ex.getMessage());
    }

    @Test
    @DisplayName("Thêm mới thất bại - Username đã tồn tại")
    void testCreate_DuplicateUsername() {
        Account acc = new Account("admin", "123", "USER", true);
        assertThrows(IllegalArgumentException.class, () -> accountService.create(acc));
    }

    @Test
    @DisplayName("Thêm mới thành công - Dữ liệu chuẩn")
    void testCreate_Success() {
        String uniqueUser = "user_" + System.currentTimeMillis();
        Account acc = new Account(uniqueUser, "pass123", "STAFF", true);

        assertDoesNotThrow(() -> accountService.create(acc));
        createdUsername = uniqueUser;
    }

    @Test
    @DisplayName("Cập nhật thất bại - Username bị null")
    void testUpdate_NullUsername() {
        Account acc = new Account(null, "123", "USER", true);
        assertThrows(RuntimeException.class, () -> accountService.update(acc));
    }

    @Test
    @DisplayName("Cập nhật thất bại - Password trống")
    void testUpdate_EmptyPassword() {
        Account acc = new Account("admin", "  ", "USER", true);
        Exception ex = assertThrows(RuntimeException.class, () -> accountService.update(acc));
        assertEquals("Password không được trống", ex.getMessage());
    }

    @Test
    @DisplayName("Xóa tài khoản - Kiểm tra ID không hợp lệ")
    void testDelete() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteById(-999));
    }

    @Test
    @DisplayName("Kiểm tra tồn tại - Username có thật")
    void testExists_True() {
        boolean exists = accountService.existsByUsername("admin");
        assertTrue(accountService.existsByUsername("admin"));
    }
}
