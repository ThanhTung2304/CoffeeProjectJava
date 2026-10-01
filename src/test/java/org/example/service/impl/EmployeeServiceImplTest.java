package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;
    private int createdId = -1;
    private String createdUsername = null;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl();
    }

    @AfterEach
    void tearDown() {

        // Xóa Employee test
        if (createdId > 0) {
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "DELETE FROM employee WHERE id = ?")) {

                ps.setInt(1, createdId);
                ps.executeUpdate();

            } catch (SQLException e) {
                // Ignore cleanup error
            }

            createdId = -1;
        }

        // Xóa Account test
        if (createdUsername != null) {
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "DELETE FROM account WHERE username = ?")) {

                ps.setString(1, createdUsername);
                ps.executeUpdate();

            } catch (SQLException e) {
                // Ignore cleanup error
            }

            createdUsername = null;
        }
    }

    /**
     * Tạo Account test để Employee có account_id hợp lệ.
     */
    private int createTestAccount() {

        String username = "test_emp_" + System.currentTimeMillis();

        Account account = new Account(
                username,
                "pass123",
                "STAFF",
                true
        );

        AccountServiceImpl accountService = new AccountServiceImpl();

        accountService.create(account);

        Account savedAccount = accountService.findByUsername(username);

        assertNotNull(savedAccount, "Account test phải được tạo thành công");

        createdUsername = username;

        return savedAccount.getId();
    }

    @Test
    @DisplayName("Lấy tất cả danh sách - Phải trả về List không null")
    void testFindAll() {

        List<Employee> list = employeeService.findAll();

        assertNotNull(list);
    }

    @Test
    @DisplayName("Thêm mới thành công - Dữ liệu hợp lệ")
    void testCreate_Success() {

        int accountId = createTestAccount();

        String uniqueName = "Test Employee " + System.currentTimeMillis();

        Employee emp = new Employee();

        emp.setName(uniqueName);
        emp.setPhone("0987654321");
        emp.setPosition("Staff");
        emp.setAccountId(accountId);

        assertDoesNotThrow(() -> employeeService.create(emp));

        Employee saved = employeeService.findAll()
                .stream()
                .filter(e -> uniqueName.equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(saved, "Nhân viên vừa tạo phải tồn tại trong DB");

        createdId = saved.getId();

        assertEquals(uniqueName, saved.getName());
        assertEquals("0987654321", saved.getPhone());
        assertEquals("Staff", saved.getPosition());
        assertEquals(accountId, saved.getAccountId());
    }

    @Test
    @DisplayName("Cập nhật thành công - Thay đổi thông tin nhân viên")
    void testUpdate_Success() {

        int accountId = createTestAccount();

        String uniqueName = "Test Update " + System.currentTimeMillis();

        Employee emp = new Employee();

        emp.setName(uniqueName);
        emp.setPhone("0987654321");
        emp.setPosition("Staff");
        emp.setAccountId(accountId);

        assertDoesNotThrow(() -> employeeService.create(emp));

        Employee saved = employeeService.findAll()
                .stream()
                .filter(e -> uniqueName.equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(saved, "Không tìm thấy nhân viên vừa tạo");

        createdId = saved.getId();

        saved.setName(uniqueName + " Updated");
        saved.setPosition("Manager");

        assertDoesNotThrow(() -> employeeService.update(saved));

        Employee updated = employeeService.findAll()
                .stream()
                .filter(e -> e.getId() == createdId)
                .findFirst()
                .orElse(null);

        assertNotNull(updated, "Nhân viên phải tồn tại sau khi cập nhật");

        assertEquals(uniqueName + " Updated", updated.getName());
        assertEquals("Manager", updated.getPosition());
        assertEquals(accountId, updated.getAccountId());
    }

    @Test
    @DisplayName("Xóa thành công - Nhân viên không còn tồn tại")
    void testDelete_Success() {

        int accountId = createTestAccount();

        String uniqueName = "Test Delete " + System.currentTimeMillis();

        Employee emp = new Employee();

        emp.setName(uniqueName);
        emp.setPhone("0987654321");
        emp.setPosition("Staff");
        emp.setAccountId(accountId);

        assertDoesNotThrow(() -> employeeService.create(emp));

        Employee saved = employeeService.findAll()
                .stream()
                .filter(e -> uniqueName.equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(saved, "Không tìm thấy nhân viên vừa tạo");

        int targetId = saved.getId();

        createdId = targetId;

        assertDoesNotThrow(() -> employeeService.deleteById(targetId));

        Employee deleted = employeeService.findAll()
                .stream()
                .filter(e -> e.getId() == targetId)
                .findFirst()
                .orElse(null);

        assertNull(deleted, "Nhân viên đã xóa không được tồn tại trong DB");

        // Employee đã được xóa nên không cần cleanup Employee nữa
        createdId = -1;
    }
}