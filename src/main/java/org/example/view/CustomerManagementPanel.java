package org.example.view;

import org.example.controller.CustomerController;
import org.example.entity.Customer;
import org.example.util.ExportToExcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class CustomerManagementPanel extends JPanel {

    // ===== COLORS =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_AMBER = new Color(0xF59E0B);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_SLATE = new Color(0x64748B);

    private static final Color BADGE_ACTIVE = new Color(0xDCFCE7);
    private static final Color BADGE_STOP = new Color(0xFEE2E2);

    // ===== FONTS =====
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final CustomerController controller = new CustomerController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCount;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadData();
    }

    /* ================= HEADER ================= */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("👥 Quản Lý Khách Hàng");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        rowCount = new JLabel("0 khách");
        rowCount.setForeground(Color.LIGHT_GRAY);

        p.add(title, BorderLayout.WEST);
        p.add(rowCount, BorderLayout.EAST);

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
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngưng"});

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnSearch);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_SLATE);
        JButton btnExport = createButton("Excel", BTN_BLUE);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        right.add(btnExport);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // events
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnExport.addActionListener(e -> ExportToExcel.export(table, "khachhang.xlsx"));

        return bar;
    }

    /* ================= TABLE ================= */
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Mã", "Tên", "SĐT", "Email", "Điểm", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(ROW_SELECTED);

        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int r, int c) {

                if (c == 6 && v != null) {
                    boolean active = v.toString().equals("Hoạt động");
                    JLabel lb = new JLabel(active ? "● Hoạt động" : "● Ngưng");
                    lb.setOpaque(true);
                    lb.setHorizontalAlignment(CENTER);
                    lb.setBackground(active ? BADGE_ACTIVE : BADGE_STOP);
                    return lb;
                }

                super.getTableCellRendererComponent(t, v, sel, f, r, c);
                setBackground(sel ? ROW_SELECTED : (r % 2 == 0 ? ROW_ODD : ROW_EVEN));
                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0x334155));
        th.setForeground(Color.WHITE);

        return new JScrollPane(table);
    }

    /* ================= LOAD ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        List<Customer> list = controller.search(
                txtSearch.getText(),
                Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
        );

        int i = 1;
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    i++,
                    c.getCode(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getPoint(),
                    c.getStatus() == 1 ? "Hoạt động" : "Ngưng"
            });
        }

        rowCount.setText(list.size() + " khách hàng");
    }

    /* ================= CRUD ================= */
    private void openAddDialog() {
        JOptionPane.showMessageDialog(this, "Reuse code cũ của bạn");
    }

    private void openEditDialog() {
        JOptionPane.showMessageDialog(this, "Reuse code cũ của bạn");
    }

    private void deleteCustomer() {
        JOptionPane.showMessageDialog(this, "Reuse code cũ của bạn");
    }

    /* ================= BUTTON ================= */
    private JButton createButton(String text, Color c) {
        JButton b = new JButton(text);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}