package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.repository.OrderDetailRepository;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderDetailRepositoryImpl;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo = new OrderRepositoryImpl();
    private final OrderDetailRepository detailRepo = new OrderDetailRepositoryImpl();

    private static final AtomicInteger ORDER_COUNTER = new AtomicInteger(0);

    @Override
    public Order createOrder(List<OrderDetail> details, String note) {
        if (details == null || details.isEmpty())
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 sản phẩm");

        double total = details.stream()
                .mapToDouble(OrderDetail::getSubtotal)
                .sum();

        String code = generateOrderCode();

        Order order = new Order(code, note);
        order.setTotalAmount(total);
        order.setStatus("PENDING");

        orderRepo.save(order);

        for (OrderDetail d : details) {
            d.setOrderId(order.getId());
        }
        detailRepo.saveAll(details);

        order.setDetails(details);
        return order;
    }

    @Override
    public void cancelOrder(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order == null)
            throw new IllegalArgumentException("Không tìm thấy đơn hàng ID = " + orderId);
        if ("COMPLETED".equals(order.getStatus()))
            throw new IllegalStateException("Không thể hủy đơn đã hoàn thành");
        if ("CANCELLED".equals(order.getStatus()))
            throw new IllegalStateException("Đơn hàng đã bị hủy trước đó");
        orderRepo.updateStatus(orderId, "CANCELLED");
    }

    @Override
    public void completeOrder(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order == null)
            throw new IllegalArgumentException("Không tìm thấy đơn hàng ID = " + orderId);
        if ("CANCELLED".equals(order.getStatus()))
            throw new IllegalStateException("Không thể hoàn thành đơn đã hủy");
        if ("COMPLETED".equals(order.getStatus()))
            throw new IllegalStateException("Đơn hàng đã hoàn thành trước đó");
        orderRepo.updateStatus(orderId, "COMPLETED");
    }

    @Override
    public Order getOrderWithDetails(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order != null) {
            order.setDetails(detailRepo.findByOrderId(orderId));
        }
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public void deleteOrder(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order == null)
            throw new IllegalArgumentException("Không tìm thấy đơn hàng ID = " + orderId);
        if ("COMPLETED".equals(order.getStatus()))
            throw new IllegalStateException("Không thể xóa đơn đã hoàn thành");
        orderRepo.delete(orderId);
    }

    private String generateOrderCode() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int seq = ORDER_COUNTER.incrementAndGet() % 10000;
        return String.format("ORD-%s-%04d", timestamp, seq);
    }
}
