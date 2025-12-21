package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends JPanel {

    private final Map<String, JButton> menuMap = new LinkedHashMap<>();

    public Sidebar(String role) {
        setLayout(new GridLayout(0, 1, 0, 8));
        setPreferredSize(new Dimension(220, 0));
        setBackground(new Color(45, 52, 70));

        if ("ADMIN".equals(role)) {
            addMenu("Quản Lý Tài Khoản");
        }
        addMenu("Quản Lý Nhân Viên");
        addMenu("Quản Lý Khách Hàng");
        addMenu("Quản Lý Sản Phẩm");
        addMenu("Đặt Bàn");
        addMenu("Đăng xuất");
    }

    private void addMenu(String title) {
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 52, 70));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 10));

        menuMap.put(title, btn);
        add(btn);
    }

    public JButton getMenu(String title) {
        return menuMap.get(title);
    }

    public void setActiveMenu(String title) {
        menuMap.forEach((k, v) ->
                v.setBackground(k.equals(title)
                        ? new Color(70, 90, 160)
                        : new Color(45, 52, 70)));
    }


}
