package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends JScrollPane {

    private final JPanel menuPanel = new JPanel();
    private final Map<String, JButton> menuMap = new LinkedHashMap<>();

    private static final Color BG = new Color(45, 52, 70);
    private static final Color BG_ACTIVE = new Color(70, 90, 160);
    private static final Color BG_HOVER = new Color(60, 70, 100);

    public Sidebar(String role) {

        /* ===== MENU PANEL ===== */
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setViewportView(menuPanel);

        setPreferredSize(new Dimension(230, 0));
        setBorder(null);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        /* ===== MENU ===== */
        addMenu("Thống Kê");

        if ("ADMIN".equals(role)) {
            addMenu("Quản Lý Tài Khoản");
        }

        addMenu("Quản Lý Nhân Viên");
        addMenu("Quản Lý Ca Làm");
        addMenu("Quản Lý Lịch Làm");
        addMenu("Quản Lý Khách Hàng");
        addMenu("Quản Lý Sản Phẩm");
        addMenu("Quản Lý Tồn Kho");
        addMenu("Công Thức Pha Chế");
        addMenu("Đặt Bàn");
        addMenu("Quản Lý Bàn");
        addMenu("Quản Lý Voucher");
        addMenu("Quản Lý Đơn Hàng");

        add(Box.createVerticalGlue());

        addMenu("Đăng xuất");
    }

    /* ================= MENU ITEM ================= */
    private void addMenu(String title) {

        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 10));

        /* ===== HOVER EFFECT ===== */
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != BG_ACTIVE) {
                    btn.setBackground(BG_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != BG_ACTIVE) {
                    btn.setBackground(BG);
                }
            }
        });

        menuMap.put(title, btn);
        menuPanel.add(btn);
        menuPanel.add(Box.createVerticalStrut(4));
    }

    /* ================= API CŨ ================= */
    public JButton getMenu(String title) {
        return menuMap.get(title);
    }

    public void setActiveMenu(String title) {
        menuMap.forEach((k, v) -> {
            boolean active = k.equals(title);
            v.setBackground(active ? BG_ACTIVE : BG);
        });
    }
}
