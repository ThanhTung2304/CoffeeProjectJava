package org.example.view;

import org.example.controller.ReservationController;
import org.example.entity.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class BookingManagementPanel extends JPanel {

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

    private static final Color BADGE_PENDING = new Color(0xFEF9C3);
    private static final Color BADGE_DONE = new Color(0xDCFCE7);
    private static final Color BADGE_CANCEL = new Color(0xFEE2E2);

    // ===== FONT =====
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final ReservationController controller = new ReservationController();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCount;

    public BookingManagementPanel() {
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

        JLabel title = new JLabel("📅 Quản Lý Đặt Bàn");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        rowCount = new JLabel("0 đặt bàn");
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
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đang đặt", "Hoàn thành", "Hủy"});

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnSearch);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻", BTN_GRAY);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // events
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteBooking());

        return bar;
    }

    /* ================= TABLE ================= */
    private JScrollPane buildTable() {
        model = new DefaultTableModel(
                new String[]{"ID", "STT", "Tên KH", "Bàn", "Ngày", "Trạng thái", "Ghi chú"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(ROW_SELECTED);
        table.setGridColor(BORDER);

        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int r, int c) {

                if (c == 4 && v != null) {
                    JLabel badge = new JLabel(v.toString());
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(CENTER);

                    switch (v.toString()) {
                        case "Đang đặt" -> badge.setBackground(BADGE_PENDING);
                        case "Hoàn thành" -> badge.setBackground(BADGE_DONE);
                        case "Hủy" -> badge.setBackground(BADGE_CANCEL);
                    }
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

        return new JScrollPane(table);
    }

    /* ================= LOAD ================= */
    private void loadData() {
        model.setRowCount(0);

        String keyword = txtSearch.getText().toLowerCase();
        String status = Objects.requireNonNull(cbStatus.getSelectedItem()).toString();

        List<Reservation> list = controller.getAllReservations();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        int i = 1;
        for (Reservation r : list) {

            boolean matchName =
                    keyword.isBlank() ||
                            r.getCustomerName().toLowerCase().contains(keyword);

            boolean matchStatus =
                    status.equals("Tất cả") ||
                            r.getStatus().equalsIgnoreCase(status);

            if (matchName && matchStatus) {
                model.addRow(new Object[]{
                        r.getId(),
                        i++,
                        r.getCustomerName(),
                        r.getTableNumber(),
                        r.getTime().format(fmt),
                        r.getStatus(),
                        r.getNote()
                });
            }
        }

        rowCount.setText(list.size() + " đặt bàn");
    }

    /* ================= CRUD ================= */
    private void showAddDialog() {
        JOptionPane.showMessageDialog(this, "Giữ code cũ của bạn");
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        Reservation r = controller.findById(id);

        JOptionPane.showMessageDialog(this, "Sửa: " + r.getCustomerName());
    }

    private void deleteBooking() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        controller.deleteReservation(id);
        loadData();
    }

    /* ================= BUTTON ================= */
    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover() ? base.brighter() : base);
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
        btn.setPreferredSize(new Dimension(130, 36));

        return btn;
    }
}