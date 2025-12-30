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

    private String currentModuleTitle = "Thống Kê Tổng Quan";

    /* ===== CARD KEYS ===== */
    private static final String SCREEN_STATISTIC      = "statistic";
    private static final String SCREEN_ACCOUNTS       = "accounts";
    private static final String SCREEN_CUSTOMERS      = "customers";
    private static final String SCREEN_EMPLOYEES      = "employees";
    private static final String SCREEN_SHIFTS         = "shifts";
    private static final String SCREEN_WORK_SCHEDULE  = "work_schedule";
    private static final String SCREEN_PRODUCTS       = "products";
    private static final String SCREEN_BOOKING        = "booking";
    private static final String SCREEN_SETTINGS       = "settings";

    public MainFrame(String username, String role) {
        setTitle("Hệ Thống Quản Lý Bán Cà Phê");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        header = new Header(username, role);
        sideBar = new Sidebar(role);

        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        initScreens();
        bgContent.add(contentPanel, BorderLayout.CENTER);

        add(sideBar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(bgContent, BorderLayout.CENTER);
        add(new Footer(), BorderLayout.SOUTH);

        initMenuActions();
        initHeaderActions();

        showScreen(SCREEN_STATISTIC, "Thống Kê Tổng Quan");
    }

    /* ===== INIT SCREENS ===== */
    private void initScreens() {
        contentPanel.add(new StatisticPanel(), SCREEN_STATISTIC);
        contentPanel.add(new AccountManagementPanel(), SCREEN_ACCOUNTS);
        contentPanel.add(new CustomerManagementPanel(), SCREEN_CUSTOMERS);
        contentPanel.add(new EmployeeManagementPanel(), SCREEN_EMPLOYEES);
        contentPanel.add(new ShiftManagementPanel(), SCREEN_SHIFTS);
        contentPanel.add(new WorkSchedulePanel(), SCREEN_WORK_SCHEDULE);
        contentPanel.add(new ProductManagementPanel(), SCREEN_PRODUCTS);
        contentPanel.add(new BookingManagementPanel(), SCREEN_BOOKING);
        contentPanel.add(createSettingScreen(), SCREEN_SETTINGS);
    }

    private JPanel createSettingScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Cài Đặt Hệ Thống", SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    /* ===== SIDEBAR ACTIONS ===== */
    private void initMenuActions() {

        bindMenu("Thống Kê", SCREEN_STATISTIC, "Thống Kê Tổng Quan");
        bindMenu("Quản Lý Tài Khoản", SCREEN_ACCOUNTS, "Quản Lý Tài Khoản");
        bindMenu("Quản Lý Khách Hàng", SCREEN_CUSTOMERS, "Quản Lý Khách Hàng");
        bindMenu("Quản Lý Nhân Viên", SCREEN_EMPLOYEES, "Quản Lý Nhân Viên");
        bindMenu("Quản Lý Ca Làm", SCREEN_SHIFTS, "Quản Lý Ca Làm");
        bindMenu("Quản Lý Lịch Làm", SCREEN_WORK_SCHEDULE, "Quản Lý Lịch Làm Việc");
        bindMenu("Quản Lý Sản Phẩm", SCREEN_PRODUCTS, "Quản Lý Sản Phẩm");
        bindMenu("Đặt Bàn", SCREEN_BOOKING, "Đặt Bàn");
        bindMenu("Cài Đặt", SCREEN_SETTINGS, "Cài Đặt Hệ Thống");

        JButton logout = sideBar.getMenu("Đăng xuất");
        if (logout != null) {
            logout.addActionListener(e -> {
                new LoginForm().setVisible(true);
                dispose();
            });
        }
    }

    private void bindMenu(String menuText, String screenKey, String title) {
        JButton btn = sideBar.getMenu(menuText);
        if (btn != null) {
            btn.addActionListener(e -> showScreen(screenKey, title));
        }
    }

    /* ===== HEADER ACTIONS ===== */
    private void initHeaderActions() {
        header.getBtnAdd().addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Thêm tại: " + currentModuleTitle));

        header.getBtnEdit().addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Sửa tại: " + currentModuleTitle));
    }

    /* ===== SHOW SCREEN ===== */
    private void showScreen(String cardKey, String moduleTitle) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(moduleTitle);
        cardLayout.show(contentPanel, cardKey);

        boolean isStatistic = SCREEN_STATISTIC.equals(cardKey);
        header.getBtnAdd().setVisible(!isStatistic);
        header.getBtnEdit().setVisible(!isStatistic);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame("admin", "ADMIN").setVisible(true)
        );
    }
}
