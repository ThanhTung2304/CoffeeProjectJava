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



    /* ===== CARD KEYS ===== */
    private static final String SCREEN_STATISTIC = "statistic";
    private static final String SCREEN_ACCOUNTS  = "accounts";
    private static final String SCREEN_CUSTOMERS = "customers";
    private static final String SCREEN_EMPLOYEES = "employees";
    private static final String SCREEN_SHIFTS         = "shifts";
    private static final String SCREEN_WORK_SCHEDULE  = "work_schedule";
    private static final String SCREEN_PRODUCTS  = "products";
    private static final String SCREEN_BOOKING   = "booking";
    private static final String SCREEN_TABLES   = "tables";
    private static final String SCREEN_VOUCHERS   = "vouches";
    private static final String SCREEN_SETTINGS  = "settings";
    private static final String SCREEN_INVENTORY = "inventory";
    private static final String SCREEN_RECIPE = "recipe";
    private static final String SCREEN_ORDERS = "orders";
    private static final String SCREEN_ORDER_DETAIL = "order_detail";




    private String currentModuleTitle = "Thống Kê Tổng Quan";

    public MainFrame(String username, String role) {
        setTitle("Hệ Thống Quản Lý Bán Cà Phê");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        /* ===== HEADER + SIDEBAR ===== */
        header = new Header(username, role);
        sideBar = new Sidebar(role);

        /* ===== CONTENT ===== */
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

    /* INIT SCREENS */
    private void initScreens() {

        /* ===== PANELS ===== */
        StatisticPanel statisticPanel = new StatisticPanel();


        contentPanel.add(new AccountManagementPanel(), SCREEN_ACCOUNTS);
        contentPanel.add(new CustomerManagementPanel(), SCREEN_CUSTOMERS);
        contentPanel.add(new EmployeeManagementPanel(), SCREEN_EMPLOYEES);
        contentPanel.add(new ShiftManagementPanel(), SCREEN_SHIFTS);
        contentPanel.add(new WorkSchedulePanel(), SCREEN_WORK_SCHEDULE);
        contentPanel.add(new ProductManagementPanel(), SCREEN_PRODUCTS);
        contentPanel.add(new BookingManagementPanel(), SCREEN_BOOKING);
        contentPanel.add(new TableManagementPanel(), SCREEN_TABLES);
        contentPanel.add(new VoucherManagementPanel(), SCREEN_VOUCHERS);
        contentPanel.add(statisticPanel, SCREEN_STATISTIC);
        contentPanel.add(createSettingScreen(), SCREEN_SETTINGS);
        contentPanel.add(new InventoryManagementPanel(), SCREEN_INVENTORY);
        contentPanel.add(new RecipeManagementPanel(), SCREEN_RECIPE);

        contentPanel.add(new OrderManagementPanel(), SCREEN_ORDERS);

        // Mặc định mở thống kê
        showScreen(SCREEN_STATISTIC, "Thống Kê Tổng Quan");
    }

    private JPanel createSettingScreen() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 22, 22));

        JLabel lbl = new JLabel("Cài Đặt Hệ Thống", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(120, 120, 160));

        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    /* SIDEBAR ACTIONS*/
    private void initMenuActions() {

        JButton btn;

        btn = sideBar.getMenu("Thống Kê");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_STATISTIC, "Thống Kê Tổng Quan"));
        }

        btn = sideBar.getMenu("Quản Lý Tài Khoản");
        if (btn != null) {
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

        btn = sideBar.getMenu("Quản Lý Ca Làm");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_SHIFTS, "Quản Lý Ca Làm"));
        }

        btn = sideBar.getMenu("Quản Lý Lịch Làm");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_WORK_SCHEDULE, "Quản Lý Lịch Làm"));
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

        btn = sideBar.getMenu("Quản Lý Bàn");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_TABLES, "Quản Lý Bàn"));
        }

        btn = sideBar.getMenu("Quản Lý Voucher");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_VOUCHERS, "Quản Lý Voucher"));
        }

        btn = sideBar.getMenu("Cài Đặt");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_SETTINGS, "Cài Đặt Hệ Thống"));
        }

        btn = sideBar.getMenu("Quản Lý Tồn Kho");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_INVENTORY, "Quản Lý Tồn Kho"));
        }
        btn = sideBar.getMenu("Công Thức Pha Chế");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_RECIPE, "Công Thức Pha Chế"));
        }
        btn = sideBar.getMenu("Quản Lý Đơn Hàng");
        if (btn != null) {
            btn.addActionListener(e ->
                    showScreen(SCREEN_ORDERS, "Quản Lý Đơn Hàng"));
        }


        btn = sideBar.getMenu("Đăng xuất");
        if (btn != null) {
            btn.addActionListener(e -> {
                new LoginForm().setVisible(true);
                dispose();
            });
        }
    }

    /*HEADER ACTIONS*/
    private void initHeaderActions() {

        header.getBtnAdd().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Thêm tại: " + currentModuleTitle));

        header.getBtnEdit().addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Sửa tại: " + currentModuleTitle));
    }

    /*SHOW SCREEN*/
    private void showScreen(String cardKey, String moduleTitle) {

        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(moduleTitle);
        cardLayout.show(contentPanel, cardKey);

        // Ẩn nút Add/Edit ở màn hình thống kê
        boolean isStatistic = SCREEN_STATISTIC.equals(cardKey);
        header.getBtnAdd().setVisible(!isStatistic);
        header.getBtnEdit().setVisible(!isStatistic);
    }
    // ================= ORDER DETAIL =================
    public void showOrderDetail(org.example.entity.Order order) {

        // Thêm màn hình chi tiết đơn hàng
        contentPanel.add(
                new OrderDetailPanel(order),
                SCREEN_ORDER_DETAIL
        );

        // Chuyển sang màn hình chi tiết
        showScreen(SCREEN_ORDER_DETAIL, "Chi Tiết Đơn Hàng");
    }

    /*ROUNDED PANEL*/
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, radius, radius);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);
        }
    }


    /*TEST MAIN*/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame("admin", "ADMIN").setVisible(true)
        );
    }
}