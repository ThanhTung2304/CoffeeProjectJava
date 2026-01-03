package org.example.view;

import org.example.entity.Order;

import javax.swing.*;
import java.awt.*;

public class OrderDetailPanel extends JPanel {

    private final Order order;

    public OrderDetailPanel(Order order) {
        this.order = order;
        setLayout(new BorderLayout(10, 10));
        initUI();
    }

    private void initUI() {

        /* ===== TITLE ===== */
        JLabel title = new JLabel(
                "CHI TIẾT ĐƠN HÀNG #" + order.getId(),
                SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        /* ===== ORDER ITEM PANEL ===== */
        OrderItemPanel itemPanel =
                new OrderItemPanel(order.getId());

        add(itemPanel, BorderLayout.CENTER);
    }
}
