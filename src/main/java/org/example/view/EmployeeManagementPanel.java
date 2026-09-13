package org.example.view;

import org.example.controller.EmployeeController;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.event.DataChangeEventBus;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class EmployeeManagementPanel extends JPanel {

    // ===== COLORS =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);
    private static final Color BORDER = new Color(0xE2E8F0);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_AMBER = new Color(0xF59E0B);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_GRAY = new Color(0x64748B);
    private static final Color BTN_PURPLE = new Color(0x8B5CF6);

    private static final Color BADGE_ACTIVE = new Color(0xDCFCE7);
    private static final Color BADGE_ACTIVE_FG = new Color(0x166534);
    private static final Color BADGE_LOCKED = new Color(0xFEE2E2);
    private static final Color BADGE_LOCKED_FG = new Color(0x991B1B);

    // ===== FONT =====
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbPosition;
    private JLabel rowCountLabel;

    private final EmployeeController controller = new EmployeeController();
    private final AccountRepository accountRepository = new AccountRepositoryImpl();

    private final DataChangeEventBus.DataChangeListener dataListener = this::loadData;

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadData();
        DataChangeEventBus.onRegister(dataListener);
    }

    public void cleanup() {
        DataChangeEventBus.onUnregister(dataListener);
    }

    /* ================= HEADER ================= */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("\uD83D\uDC68\u200D\uD83D\uDCBC");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Quản Lý Nhân Viên");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Quản lý nhân viên và tài khoản liên kết");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(titleLabel);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        p.add(left, BorderLayout.WEST);

        rowCountLabel = new JLabel("0 nhân viên");
        rowCountLabel.setFont(FONT_BOLD);
        rowCountLabel.setForeground(new Color(0xCBD5E1));
        p.add(rowCountLabel, BorderLayout.EAST);

        return p;
    }

    /* ================= CENTER ================= */
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(16, 20, 20, 20));
        p.setBackground(BG);

        p.add(buildControl(), BorderLayout.NORTH);
        p.add(buildTable(), BorderLayout.CENTER);

        return p;
    }

    /* ================= CONTROL ================= */
    private JPanel buildControl() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên nhân viên...");

        cbPosition = new JComboBox<>(new String[]{"Tất cả", "Staff", "Admin"});
        cbPosition.setPreferredSize(new Dimension(130, 36));

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbPosition);
        left.add(btnSearch);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_GRAY);
        JButton btnViewAccount = createButton("🔗 Xem Tài Khoản", BTN_PURPLE);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        right.add(btnViewAccount);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        btnSearch.addActionListener(e -> loadData());
        cbPosition.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbPosition.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e ->
                new EmployeeForm(null, this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnViewAccount.addActionListener(e -> navigateToLinkedAccount());

        return bar;
    }

    /* ================= TABLE ================= */
    private JScrollPane buildTable() {
        model = new DefaultTableModel(
                new String[]{
                        "ID", "STT", "Tên Nhân Viên", "SĐT", "Chức Vụ",
                        "Username", "Phân Quyền", "Trạng Thái TK",
                        "Ngày tạo", "Cập nhật"
                }, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(ROW_SELECTED);
        table.setGridColor(BORDER);
        table.setAutoCreateRowSorter(true);

        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int r, int c) {

                if (c == 6 && v != null && !v.toString().isEmpty()) {
                    boolean active = v.toString().equals("Hoạt động");
                    JLabel badge = new JLabel(active ? "● Hoạt động" : "● Khóa");
                    badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(CENTER);
                    badge.setBackground(sel ? ROW_SELECTED : (active ? BADGE_ACTIVE : BADGE_LOCKED));
                    badge.setForeground(active ? BADGE_ACTIVE_FG : BADGE_LOCKED_FG);
                    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
                    return badge;
                }

                super.getTableCellRendererComponent(t, v, sel, f, r, c);
                setBackground(sel ? ROW_SELECTED : (r % 2 == 0 ? ROW_ODD : ROW_EVEN));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0x334155));
        th.setForeground(Color.WHITE);
        th.setFont(FONT_BOLD);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToLinkedAccount();
                }
            }
        });

        return new JScrollPane(table);
    }

    /* ================= LOAD ================= */
    private void loadData() {
        model.setRowCount(0);

        String keyword = txtSearch.getText().toLowerCase();
        String pos = Objects.requireNonNull(cbPosition.getSelectedItem()).toString();

        List<Employee> list = controller.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        int count = 0;
        int stt = 1;
        for (Employee emp : list) {
            boolean matchName = keyword.isBlank() || emp.getName().toLowerCase().contains(keyword);
            boolean matchPos = pos.equals("Tất cả") || (emp.getPosition() != null && emp.getPosition().equalsIgnoreCase(pos));

            if (matchName && matchPos) {
                Account acc = null;
                if (emp.getAccountId() != null) {
                    acc = accountRepository.findById(emp.getAccountId().longValue()).orElse(null);
                }

                String username    = acc != null ? acc.getUsername() : "";
                String role        = acc != null ? acc.getRole() : "";
                String accStatus   = acc != null ? (acc.isActive() ? "Hoạt động" : "Khóa") : "";

                model.addRow(new Object[]{
                        emp.getId(),
                        stt++,
                        emp.getName(),
                        emp.getPhone(),
                        emp.getPosition(),
                        username,
                        role,
                        accStatus,
                        emp.getCreatedTime() == null ? "" : emp.getCreatedTime().format(fmt),
                        emp.getUpdateTime() == null ? "" : emp.getUpdateTime().format(fmt)
                });
                count++;
            }
        }

        rowCountLabel.setText(count + " nhân viên");
    }

    /* ================= NAVIGATION ================= */
    private void navigateToLinkedAccount() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        int empId = (int) model.getValueAt(modelRow, 0);
        Employee emp = controller.findById(empId);

        if (emp == null || emp.getAccountId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Nhân viên này chưa liên kết tài khoản nào!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MainFrame mainFrame = MainFrame.getInstance();
        if (mainFrame != null) {
            mainFrame.showScreen(
                    MainFrame.SCREEN_ACCOUNTS,
                    "Quản Lý Tài Khoản",
                    "Quản Lý Tài Khoản"
            );
        }
    }

    /* ================= CRUD ================= */
    private void editEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);
        Employee emp = controller.findById(id);
        new EmployeeForm(emp, this::loadData).setVisible(true);
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData();
        }
    }

    /* ================= BUTTON STYLE ================= */
    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 36));
        return btn;
    }
}
