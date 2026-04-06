package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Sidebar extends JScrollPane {

    private final JPanel menuPanel = new JPanel();
    private final Map<String, JButton> menuMap = new LinkedHashMap<>();
    private final Map<String, List<String>> rolePermissions = new HashMap<>();

    private static final Color BG = new Color(45, 52, 70);
    private static final Color BG_ACTIVE = new Color(70, 90, 160);
    private static final Color BG_HOVER = new Color(60, 70, 100);

    public Sidebar(String role) {

        /* ===== INIT UI ===== */
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        setViewportView(menuPanel);
        setPreferredSize(new Dimension(230, 0));
        setBorder(null);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        /* ===== INIT PERMISSION ===== */
        initPermissions();

        /* ===== LOAD MENU THEO ROLE ===== */
        List<String> menus = rolePermissions.getOrDefault(role, new ArrayList<>());

        for (String menu : menus) {
            addMenu(menu);
        }

        /* ===== ĐẨY ĐĂNG XUẤT XUỐNG CUỐI ===== */
//        menuPanel.add(Box.createVerticalGlue());
//        addMenu("Đăng xuất");
    }

    /* ================= PERMISSION ================= */
    private void initPermissions() {

        rolePermissions.put("ADMIN", Arrays.asList(
                "Thống Kê",
                "Quản Lý Tài Khoản",
                "Quản Lý Nhân Viên",
                "Quản Lý Ca Làm",
                "Quản Lý Lịch Làm",
                "Quản Lý Khách Hàng",
                "Sản Phẩm",
                "Quản Lý Tồn Kho",
                "Công Thức Pha Chế",
                "Đặt Bàn",
                "Quản Lý Bàn",
                "Voucher",
                "Đơn Hàng"
        ));

        rolePermissions.put("STAFF", Arrays.asList(
                "Đặt Bàn",
                "Đơn Hàng",
                "Quản Lý Khách Hàng",
                "Quản Lý Bàn"
        ));

        rolePermissions.put("USER", Arrays.asList(
                "Đặt Bàn",
                "Sản Phẩm",
                "Voucher"
        ));
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

        /* ===== HOVER ===== */
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

    /* ================= API ================= */

    public JButton getMenu(String title) {
        return menuMap.get(title);
    }

    public void setActiveMenu(String title) {
        menuMap.forEach((k, v) -> {
            boolean active = k.equals(title);
            v.setBackground(active ? BG_ACTIVE : BG);
        });
    }

    public Set<String> getAvailableMenus() {
        return menuMap.keySet();
    }
}