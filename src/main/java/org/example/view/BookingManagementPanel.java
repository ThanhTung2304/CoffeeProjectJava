package org.example.view;

import org.example.controller.ReservationController;
import org.example.entity.Customer;
import org.example.entity.Reservation;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class BookingManagementPanel extends JPanel {

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

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final ReservationController controller = new ReservationController();
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl();

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

        JLabel title = new JLabel("\uD83D\uDCC5 Quản Lý Đặt Bàn");
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
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên khách hàng...");

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đang đặt", "Đã đặt"});
        cbStatus.setFont(FONT_BODY);

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
                new String[]{"ID", "STT", "Tên KH", "Mã KH", "Bàn", "Ngày giờ", "Trạng thái", "Ghi chú"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
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

                if (c == 6 && v != null) {
                    JLabel badge = new JLabel(v.toString());
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(CENTER);
                    badge.setFont(FONT_BOLD);

                    switch (v.toString()) {
                        case "Đang đặt" -> {
                            badge.setBackground(BADGE_PENDING);
                            badge.setForeground(new Color(0x854D0E));
                        }
                        case "Đã đặt" -> {
                            badge.setBackground(BADGE_DONE);
                            badge.setForeground(new Color(0x166534));
                        }
                    }
                    return badge;
                }

                super.getTableCellRendererComponent(t, v, sel, f, r, c);
                setBackground(sel ? ROW_SELECTED : (r % 2 == 0 ? ROW_ODD : ROW_EVEN));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Đang đặt", "Đã đặt"});
        statusCombo.setFont(FONT_BODY);

        DefaultCellEditor statusEditor = new DefaultCellEditor(statusCombo) {
            private boolean alreadySaved = false;

            @Override
            public boolean stopCellEditing() {
                alreadySaved = true;
                int row = table.getEditingRow();
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) model.getValueAt(modelRow, 0);
                    String newStatus = (String) statusCombo.getSelectedItem();

                    Reservation r = controller.getReservationById(id);
                    if (r != null && !r.getStatus().equals(newStatus)) {
                        r.setStatus(newStatus);
                        controller.updateReservation(r);
                        loadData();
                    }
                }
                return super.stopCellEditing();
            }

            @Override
            public void cancelCellEditing() {
                alreadySaved = false;
                super.cancelCellEditing();
            }
        };
        table.getColumnModel().getColumn(6).setCellEditor(statusEditor);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col == table.convertColumnIndexToView(6)) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        table.editCellAt(row, col);
                    }
                }
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
        List<Customer> allCustomers = customerRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        int i = 1;
        for (Reservation r : list) {
            boolean matchName =
                    keyword.isBlank() ||
                            (r.getCustomerName() != null && r.getCustomerName().toLowerCase().contains(keyword));

            boolean matchStatus =
                    status.equals("Tất cả") ||
                            r.getStatus().equalsIgnoreCase(status);

            if (matchName && matchStatus) {
                String code = "";
                if (r.getCustomerId() != null) {
                    Customer c = allCustomers.stream()
                            .filter(cust -> cust.getId() == r.getCustomerId())
                            .findFirst().orElse(null);
                    if (c != null) code = c.getCode();
                }

                model.addRow(new Object[]{
                        r.getId(),
                        i++,
                        r.getCustomerName(),
                        code,
                        r.getTableNumber(),
                        r.getTime() != null ? r.getTime().format(fmt) : "",
                        r.getStatus(),
                        r.getNote() != null ? r.getNote() : ""
                });
            }
        }

        rowCount.setText(list.size() + " đặt bàn");
    }

    /* ================= ADD DIALOG ================= */
    private void showAddDialog() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có khách hàng nào trong hệ thống!\nVui lòng thêm khách hàng trước.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(window instanceof Frame ? (Frame) window : null, "Thêm đặt bàn mới", true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JComboBox<String> cbCustomer = new JComboBox<>();
        for (Customer c : customers) {
            cbCustomer.addItem(c.getCode() + " - " + c.getName());
        }
        cbCustomer.setFont(FONT_BODY);

        JTextField txtTable = new JTextField();
        JTextField txtDateTime = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        JTextField txtNote = new JTextField();

        txtTable.setFont(FONT_BODY);
        txtDateTime.setFont(FONT_BODY);
        txtNote.setFont(FONT_BODY);

        form.add(new JLabel("Khách hàng:"));  form.add(cbCustomer);
        form.add(new JLabel("Số bàn:"));       form.add(txtTable);
        form.add(new JLabel("Ngày giờ (dd-MM-yyyy HH:mm):")); form.add(txtDateTime);
        form.add(new JLabel("Ghi chú:"));      form.add(txtNote);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Lưu", BTN_GREEN);
        JButton btnCancel = createButton("Hủy", BTN_RED);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            try {
                int custIdx = cbCustomer.getSelectedIndex();
                if (custIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Customer selectedCust = customers.get(custIdx);

                String tableStr = txtTable.getText().trim();
                if (tableStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Số bàn không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int tableNumber;
                try {
                    tableNumber = Integer.parseInt(tableStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Số bàn phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(txtDateTime.getText().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Ngày giờ không đúng định dạng dd-MM-yyyy HH:mm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String note = txtNote.getText().trim();

                Reservation reservation = new Reservation(selectedCust.getName(), tableNumber, dateTime, "Đang đặt", note);
                reservation.setCustomerId(selectedCust.getId());

                controller.addReservation(reservation);
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm đặt bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /* ================= EDIT DIALOG ================= */
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt bàn cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        Reservation r = controller.getReservationById(id);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đặt bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có khách hàng nào trong hệ thống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(window instanceof Frame ? (Frame) window : null, "Sửa đặt bàn", true);
        dialog.setSize(480, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JComboBox<String> cbCustomer = new JComboBox<>();
        int selectedIdx = 0;
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            cbCustomer.addItem(c.getCode() + " - " + c.getName());
            if (r.getCustomerId() != null && c.getId() == r.getCustomerId()) {
                selectedIdx = i + 1;
            }
        }
        cbCustomer.setSelectedIndex(selectedIdx);
        cbCustomer.setFont(FONT_BODY);

        JTextField txtTable = new JTextField(String.valueOf(r.getTableNumber()));
        JTextField txtDateTime = new JTextField(r.getTime() != null ? r.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "");
        JTextField txtNote = new JTextField(r.getNote() != null ? r.getNote() : "");

        txtTable.setFont(FONT_BODY);
        txtDateTime.setFont(FONT_BODY);
        txtNote.setFont(FONT_BODY);

        form.add(new JLabel("Khách hàng:"));  form.add(cbCustomer);
        form.add(new JLabel("Số bàn:"));       form.add(txtTable);
        form.add(new JLabel("Ngày giờ (dd-MM-yyyy HH:mm):")); form.add(txtDateTime);
        form.add(new JLabel("Ghi chú:"));      form.add(txtNote);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Cập nhật", BTN_AMBER);
        JButton btnCancel = createButton("Hủy", BTN_RED);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            try {
                int custIdx = cbCustomer.getSelectedIndex();
                if (custIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Customer selectedCust = customers.get(custIdx);

                String tableStr = txtTable.getText().trim();
                if (tableStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Số bàn không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int tableNumber;
                try {
                    tableNumber = Integer.parseInt(tableStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Số bàn phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(txtDateTime.getText().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Ngày giờ không đúng định dạng dd-MM-yyyy HH:mm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String note = txtNote.getText().trim();

                r.setCustomerName(selectedCust.getName());
                r.setCustomerId(selectedCust.getId());
                r.setTableNumber(tableNumber);
                r.setTime(dateTime);
                r.setNote(note);

                controller.updateReservation(r);
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /* ================= DELETE ================= */
    private void deleteBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt bàn cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa đặt bàn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
