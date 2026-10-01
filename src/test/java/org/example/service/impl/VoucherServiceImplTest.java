package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Voucher;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VoucherServiceImplTest {

    private VoucherServiceImpl voucherService;
    private String createdCode = null;

    @BeforeEach
    void setUp() {
        voucherService = new VoucherServiceImpl();
    }

    @AfterEach
    void tearDown() {

        // Xóa Voucher test
        if (createdCode != null) {
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "DELETE FROM vouchers WHERE code = ?")) {

                ps.setString(1, createdCode);
                ps.executeUpdate();

            } catch (SQLException e) {
                // Ignore cleanup error
            }

            createdCode = null;
        }
    }

    /**
     * Tạo Voucher test hợp lệ.
     */
    private Voucher createTestVoucher(String code) {

        Voucher voucher = new Voucher();

        voucher.setCode(code);
        voucher.setDiscountType("PERCENT");
        voucher.setDiscountValue(15.0);
        voucher.setStartDate(LocalDate.now());
        voucher.setEndDate(LocalDate.now().plusDays(30));
        voucher.setStatus("Còn hiệu lực");
        voucher.setUsageLimit(100);
        voucher.setUsedCount(0);
        voucher.setNote("Voucher test");

        return voucher;
    }

    @Test
    @DisplayName("Lấy danh sách Voucher - Phải trả về List không null")
    void testSearchAll() {

        List<Voucher> list = voucherService.search("", "Tất cả");

        assertNotNull(list);
    }

    @Test
    @DisplayName("Thêm mới thành công - Dữ liệu hợp lệ")
    void testAdd_Success() {

        String code = "TEST_VC_" + System.currentTimeMillis();

        Voucher voucher = createTestVoucher(code);

        assertDoesNotThrow(() -> voucherService.add(voucher));

        createdCode = code;

        Voucher saved = voucherService.findByCode(code);

        assertNotNull(saved, "Voucher vừa tạo phải tồn tại trong DB");

        assertEquals(code, saved.getCode());
        assertEquals("Phần trăm", saved.getDiscountType());
        assertEquals(15.0, saved.getDiscountValue());
        assertEquals("Còn hiệu lực", saved.getStatus());
        assertEquals(100, saved.getUsageLimit());
        assertEquals(0, saved.getUsedCount());
    }

    @Test
    @DisplayName("Tự động cập nhật trạng thái - Voucher quá hạn")
    void testAutoUpdateStatus() {

        String code = "TEST_EX_" + System.currentTimeMillis();

        Voucher voucher = createTestVoucher(code);

        voucher.setStartDate(LocalDate.now().minusDays(10));
        voucher.setEndDate(LocalDate.now().minusDays(1));
        voucher.setStatus("Còn hiệu lực");

        assertDoesNotThrow(() -> voucherService.add(voucher));

        createdCode = code;

        // Gọi search để kích hoạt logic kiểm tra ngày hết hạn
        assertDoesNotThrow(
                () -> voucherService.search(code, "Tất cả")
        );

        Voucher updated = voucherService.findByCode(code);

        assertNotNull(updated, "Voucher phải tồn tại sau khi cập nhật");

        assertEquals("Hết hạn", updated.getStatus());
    }

    @Test
    @DisplayName("Tìm kiếm theo mã Voucher - Phải tìm thấy Voucher")
    void testSearchByCode() {

        String code = "TEST_SEARCH_" + System.currentTimeMillis();

        Voucher voucher = createTestVoucher(code);

        assertDoesNotThrow(() -> voucherService.add(voucher));

        createdCode = code;

        List<Voucher> result = voucherService.search(code, "Tất cả");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(
                result.stream()
                        .anyMatch(v -> code.equals(v.getCode())),
                "Danh sách kết quả phải chứa Voucher cần tìm"
        );
    }

    @Test
    @DisplayName("Lọc theo trạng thái - Chỉ trả về Voucher hết hạn")
    void testSearchByStatus() {
        String code = "TEST_STATUS_" + System.currentTimeMillis();

        Voucher voucher = createTestVoucher(code);

        voucher.setStartDate(LocalDate.now().minusDays(10));
        voucher.setEndDate(LocalDate.now().minusDays(1));
        voucher.setStatus("Hết hạn");

        assertDoesNotThrow(() -> voucherService.add(voucher));

        createdCode = code;

        List<Voucher> result = voucherService.search("", "Hết hạn");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(
                result.stream().anyMatch(v -> code.equals(v.getCode())),
                "Danh sách phải chứa Voucher hết hạn vừa tạo"
        );

        for (Voucher v : result) {
            assertEquals("Hết hạn", v.getStatus());
        }
    }

    @Test
    @DisplayName("Xóa Voucher - ID không hợp lệ phải báo lỗi")
    void testDelete_InvalidId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.delete(-1)
        );
    }
}
