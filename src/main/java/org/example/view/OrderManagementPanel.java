package org.example.view;

import javax.swing.*;
import java.awt.*;

public class OrderManagementPanel extends JPanel {

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lbl = new JLabel("QUẢN LÝ ĐƠN HÀNG / THANH TOÁN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));

        add(lbl, BorderLayout.CENTER);
    }
}
