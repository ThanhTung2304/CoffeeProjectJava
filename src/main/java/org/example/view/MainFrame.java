package org.example.view;


import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.Sidebar;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Header header;
    private final Sidebar sideBar;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private String currentModuleTitle = "Quản Lý Bán Cà Phê";

    // Card keys
    private static final String SCREEN_ACCOUNTS = "accounts";
    private static final String SCREEN_CUSTOMERS = "customers";
    private static final String SCREEN_EMPLOYEES = "employees";
    private static final String SCREEN_PRODUCTS = "products";
    private static final String SCREEN_BOOKING = "booking";
//    private static final String SCREEN_REPORTS = "reports";
    private static final String SCREEN_SETTINGS = "settings";

    public MainFrame(String username, String role) {
        setTitle("Hệ Thống Quản Lý Bán Cà Phê");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Header & Sidebar
        header = new Header(username, role);
        sideBar = new Sidebar(role);

        // Background content
        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        initScreens();
        bgContent.add(contentPanel, BorderLayout.CENTER);

        Footer footer = new Footer();

        add(sideBar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(bgContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        initMenuActions();
        initHeaderActions();
    }

    public MainFrame( Header header, Sidebar sideBar, JPanel contentPanel, CardLayout cardLayout) {
        this.header = header;
        this.sideBar = sideBar;
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;
    }




    // ===== Tạo các màn hình mock =====
    private void initScreens() {
        contentPanel.add(new AccountManagementPanel(), SCREEN_ACCOUNTS);

        contentPanel.add(new CustomerManagementPanel(), SCREEN_CUSTOMERS);
        contentPanel.add(new org.example.view.EmployeeManagementPanel(), SCREEN_EMPLOYEES);

        contentPanel.add(new CustomerManagementPanel(), SCREEN_CUSTOMERS);
        contentPanel.add(new EmployeeManagementPanel(), SCREEN_EMPLOYEES);

        contentPanel.add(new ProductManagementPanel(), SCREEN_PRODUCTS);
        contentPanel.add(new BookingManagementPanel(), SCREEN_BOOKING);
//        contentPanel.add(new DashboardPanel(), SCREEN_REPORTS);
        contentPanel.add(createModuleScreen(), SCREEN_SETTINGS);

        // Default
        showScreen(SCREEN_CUSTOMERS, "Quản lí bán cà phê");
    }

    private JPanel createModuleScreen() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 22, 22));

        JLabel lbl = new JLabel("Cài Đặt Hệ Thống", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(120, 120, 160));

        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    // ===== Gán action cho sidebar =====
    private void initMenuActions() {

        JButton btn;

        btn = sideBar.getMenu("Quản Lý Tài Khoản");
        if(btn != null){
            btn.addActionListener(e ->
                    showScreen(SCREEN_ACCOUNTS, "Quản Lý Tài Khoản"));
        }

        btn = sideBar.getMenu("Quản Lý Khách Hàng");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_CUSTOMERS, "Quản Lý Khách Hàng"));
        }

        btn = sideBar.getMenu("Quản Lý Nhân Viên");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_EMPLOYEES, "Quản Lý Nhân Viên"));
        }

        btn = sideBar.getMenu("Quản Lý Sản Phẩm");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_PRODUCTS, "Quản Lý Sản Phẩm"));
        }

        btn = sideBar.getMenu("Đặt Bàn");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_BOOKING, "Đặt Bàn"));
        }


        btn = sideBar.getMenu("Cài Đặt");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_SETTINGS, "Cài Đặt Hệ Thống"));
        }

        // Logout
        btn = sideBar.getMenu("Đăng xuất");
        if (btn != null) {
            btn.addActionListener(e -> {
                new LoginForm().setVisible(true);
                dispose();
            });
        }
    }

    // ===== Hành vi nút Thêm/Sửa/Export trên header =====
    private void initHeaderActions() {
        header.getBtnAdd().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thực hiện chức năng [Thêm] tại module: " + currentModuleTitle,
                        "Action", JOptionPane.INFORMATION_MESSAGE));

        header.getBtnEdit().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thực hiện chức năng [Sửa] tại module: " + currentModuleTitle,
                        "Action", JOptionPane.INFORMATION_MESSAGE));

    }

    // ===== Đổi màn hình + cập nhật header + sidebar =====
    private void showScreen(String cardKey, String moduleTitle) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(moduleTitle);
        cardLayout.show(contentPanel, cardKey);
    }

    // Panel bo tròn có bóng nhẹ dùng cho mọi màn hình
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // bóng nhẹ
            graphics.setColor(new Color(0, 0, 0, 18));
            graphics.fillRoundRect(4, 4, width - 8, height - 8,
                    arcs.width, arcs.height);

            // thân panel
            graphics.setColor(backgroundColor);
            graphics.fillRoundRect(0, 0, width - 8, height - 8,
                    arcs.width, arcs.height);
        }
    }

    // Test nhanh (chạy riêng, không qua login)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame("admin", "ADMIN").setVisible(true)
        );
    }
}

