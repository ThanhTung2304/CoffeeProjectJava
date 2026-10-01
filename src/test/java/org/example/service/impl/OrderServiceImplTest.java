package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
    }

    /**
     * Hàm phụ tạo Order phục vụ các test.
     * Sử dụng Product ID = 1 đã tồn tại trong Database.
     */
    private Order createTestOrder(String note) {
        List<OrderDetail> details = new ArrayList<>();

        details.add(
                new OrderDetail(
                        1,
                        "Black Coffee",
                        20000.0,
                        1
                )
        );

        return orderService.createOrder(details, note);
    }

    /**
     * TC1: Tạo đơn hàng thành công
     */
    @Test
    @DisplayName("TC1: Tạo đơn hàng thành công")
    void testCreateOrder() {

        List<OrderDetail> details = new ArrayList<>();

        details.add(
                new OrderDetail(
                        1,
                        "Black Coffee",
                        20000.0,
                        2
                )
        );

        Order order = assertDoesNotThrow(
                () -> orderService.createOrder(details, "Test tạo đơn")
        );

        assertNotNull(order);
        assertTrue(order.getId() > 0);

        assertEquals("PENDING", order.getStatus());

        assertNotNull(order.getDetails());
        assertFalse(order.getDetails().isEmpty());

        assertEquals(1, order.getDetails().size());
    }

    /**
     * TC2: Hủy đơn hàng
     * PENDING -> CANCELLED
     */
    @Test
    @DisplayName("TC2: Hủy đơn hàng")
    void testCancelOrder() {

        Order order = createTestOrder("Đơn để hủy");

        assertNotNull(order);
        assertTrue(order.getId() > 0);
        assertEquals("PENDING", order.getStatus());

        assertDoesNotThrow(
                () -> orderService.cancelOrder(order.getId())
        );

        Order updated = orderService.getOrderWithDetails(order.getId());

        assertNotNull(updated);
        assertEquals(order.getId(), updated.getId());
        assertEquals("CANCELLED", updated.getStatus());
    }

    /**
     * TC3: Hoàn thành đơn hàng
     * PENDING -> COMPLETED
     */
    @Test
    @DisplayName("TC3: Hoàn thành đơn hàng")
    void testCompleteOrder() {

        Order order = createTestOrder("Đơn thanh toán");

        assertNotNull(order);
        assertTrue(order.getId() > 0);
        assertEquals("PENDING", order.getStatus());

        int orderId = order.getId();

        assertDoesNotThrow(
                () -> orderService.completeOrder(orderId)
        );

        Order updated = orderService.getOrderWithDetails(orderId);

        assertNotNull(updated);
        assertEquals(orderId, updated.getId());
        assertEquals("COMPLETED", updated.getStatus());
    }

    /**
     * TC4: Lấy chi tiết đơn hàng
     */
    @Test
    @DisplayName("TC4: Lấy chi tiết đơn hàng")
    void testGetOrderWithDetails() {

        Order order = createTestOrder("Test detail");

        assertNotNull(order);
        assertTrue(order.getId() > 0);

        Order result = assertDoesNotThrow(
                () -> orderService.getOrderWithDetails(order.getId())
        );

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());

        assertNotNull(result.getDetails());
        assertFalse(result.getDetails().isEmpty());

        assertEquals(
                order.getDetails().size(),
                result.getDetails().size()
        );
    }

    /**
     * TC5: Lấy danh sách đơn hàng
     */
    @Test
    @DisplayName("TC5: Lấy danh sách đơn hàng")
    void testGetAllOrders() {

        List<Order> list = assertDoesNotThrow(
                () -> orderService.getAllOrders()
        );

        assertNotNull(list);

        // Kiểm tra các Order trả về có dữ liệu hợp lệ
        for (Order order : list) {
            assertNotNull(order);
            assertTrue(order.getId() > 0);
            assertNotNull(order.getStatus());
        }
    }
}