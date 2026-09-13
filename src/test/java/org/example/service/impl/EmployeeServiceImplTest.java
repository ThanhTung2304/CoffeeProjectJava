package org.example.service.impl;

import org.example.entity.Employee;
import org.example.config.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;
    private int createdId = -1;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl();
    }

    @AfterEach
    void tearDown() {
        if (createdId > 0) {
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM employee WHERE id = ?")) {
                ps.setInt(1, createdId);
                ps.executeUpdate();
            } catch (SQLException e) {
                // ignore cleanup error
            }
            createdId = -1;
        }
    }

    @Test
    @DisplayName("1. Test lấy danh sách nhân viên")
    void findAll() {
        List<Employee> list = employeeService.findAll();
        assertNotNull(list);
        System.out.println("Tổng số nhân viên: " + list.size());
    }

    @Test
    @DisplayName("2. Test thêm mới nhân viên")
    void create() {
        Employee emp = new Employee();
        String uniqueName = "NV " + System.currentTimeMillis() % 1000;
        emp.setName(uniqueName);
        emp.setPhone("0987654321");
        emp.setPosition("Staff");

        assertDoesNotThrow(() -> employeeService.create(emp));

        Employee latest = employeeService.findAll().stream()
                .filter(e -> e.getName().contains(uniqueName))
                .findFirst().orElse(null);
        assertNotNull(latest);
        createdId = latest.getId();
    }

    @Test
    @DisplayName("3. Test cập nhật thông tin nhân viên")
    void update() {
        Employee emp = new Employee();
        emp.setName("NV UpdateTest");
        emp.setPhone("0987654321");
        emp.setPosition("Staff");
        employeeService.create(emp);

        Employee latest = employeeService.findAll().stream()
                .filter(e -> e.getName().equals("NV UpdateTest"))
                .findFirst().orElse(null);
        assertNotNull(latest, "Không tìm thấy nhân viên vừa tạo");
        createdId = latest.getId();

        latest.setName("NV UpdateTest (Updated)");
        latest.setPosition("Manager");
        employeeService.update(latest);

        List<Employee> all = employeeService.findAll();
        boolean found = all.stream().anyMatch(e -> e.getName().contains("(Updated)"));
        assertTrue(found, "Tên nhân viên chưa được cập nhật trong DB");
    }

    @Test
    @DisplayName("4. Test xóa nhân viên")
    void deleteById() {
        Employee emp = new Employee();
        emp.setName("NV DeleteTest");
        emp.setPhone("0987654321");
        emp.setPosition("Staff");
        employeeService.create(emp);

        Employee latest = employeeService.findAll().stream()
                .filter(e -> e.getName().equals("NV DeleteTest"))
                .findFirst().orElse(null);
        assertNotNull(latest, "Không tìm thấy nhân viên vừa tạo");
        int targetId = latest.getId();

        employeeService.deleteById(targetId);
        createdId = -1;

        List<Employee> all = employeeService.findAll();
        boolean stillExists = all.stream().anyMatch(e -> e.getId() == targetId);
        assertFalse(stillExists, "Nhân viên vẫn còn tồn tại sau khi xóa");
    }
}
