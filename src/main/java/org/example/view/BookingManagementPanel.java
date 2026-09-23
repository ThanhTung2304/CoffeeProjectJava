package org.example.view;

import org.example.controller.ReservationController;
import org.example.controller.TableController;
import org.example.entity.Customer;
import org.example.entity.Reservation;
import org.example.entity.TableSeat;
import org.example.event.DataChangeEventBus;
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
    private final TableController tableController = new TableController();
    private final DataChangeEventBus.DataChangeListener dataListener = this::loadData;

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
        p.add(buildBottomButtons(), BorderLayout.SOUTH);

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

        bar.add(left, BorderLayout.WEST);

        btnSearch.addActionListener(e -> loadData());

        return bar;
    }

    /* ================= BOTTOM BUTTONS ================= */
    private JPanel buildBottomButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        bar.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_GRAY);

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(btnRefresh);

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
        List<TableSeat> allTables = tableController.getAllTables();
        List<TableSeat> availableTables = allTables.stream()
                .filter(t -> "Trống".equals(t.getStatus()))
                .toList();

        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(window instanceof Frame ? (Frame) window : null, "Thêm đặt bàn mới", true);
        dialog.setSize(550, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(16, 20, 10, 20));

        // ── Customer source selection ──
        JRadioButton rbExisting = new JRadioButton("Chọn khách hàng có sẵn", true);
        JRadioButton rbNew = new JRadioButton("Nhập thông tin khách mới");
        ButtonGroup bgCustomer = new ButtonGroup();
        bgCustomer.add(rbExisting);
        bgCustomer.add(rbNew);
        rbExisting.setFont(FONT_BODY);
        rbNew.setFont(FONT_BODY);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(rbExisting);
        radioPanel.add(rbNew);
        radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        mainPanel.add(radioPanel);

        // ── Existing customer panel ──
        JPanel existingPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        existingPanel.setOpaque(false);
        existingPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JComboBox<String> cbCustomer = new JComboBox<>();
        cbCustomer.addItem("-- Chọn khách hàng --");
        for (Customer c : customers) {
            cbCustomer.addItem(c.getCode() + " - " + c.getName());
        }
        cbCustomer.setFont(FONT_BODY);

        existingPanel.add(new JLabel("Khách hàng:"));
        existingPanel.add(cbCustomer);
        mainPanel.add(existingPanel);

        // ── New customer panel ──
        JPanel newCustomerPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        newCustomerPanel.setOpaque(false);
        newCustomerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));

        JTextField txtNewName = new JTextField();
        JTextField txtNewPhone = new JTextField();
        JTextField txtNewEmail = new JTextField();
        txtNewName.setFont(FONT_BODY);
        txtNewPhone.setFont(FONT_BODY);
        txtNewEmail.setFont(FONT_BODY);

        newCustomerPanel.add(new JLabel("Tên khách:"));
        newCustomerPanel.add(txtNewName);
        newCustomerPanel.add(new JLabel("Số điện thoại:"));
        newCustomerPanel.add(txtNewPhone);
        newCustomerPanel.add(new JLabel("Email (có thể bỏ trống):"));
        newCustomerPanel.add(txtNewEmail);
        newCustomerPanel.setVisible(false);
        mainPanel.add(newCustomerPanel);

        rbExisting.addActionListener(e -> {
            existingPanel.setVisible(true);
            newCustomerPanel.setVisible(false);
            dialog.pack();
        });
        rbNew.addActionListener(e -> {
            existingPanel.setVisible(false);
            newCustomerPanel.setVisible(true);
            dialog.pack();
        });

        // ── Table selection - show only available tables ──
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setOpaque(false);
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lblTableTitle = new JLabel("Chọn bàn trống:");
        lblTableTitle.setFont(FONT_BOLD);
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);

        JComboBox<String> cbTable = new JComboBox<>();
        cbTable.setFont(FONT_BODY);

        List<TableSeat> floor1Available = allTables.stream()
                .filter(t -> t.getFloor() == 1 && "Trống".equals(t.getStatus()))
                .toList();
        List<TableSeat> floor2Available = allTables.stream()
                .filter(t -> t.getFloor() == 2 && "Trống".equals(t.getStatus()))
                .toList();

        if (floor1Available.isEmpty() && floor2Available.isEmpty()) {
            cbTable.addItem("Không có bàn trống nào");
        } else {
            if (!floor1Available.isEmpty()) {
                cbTable.addItem("── Tầng 1 ──");
                for (TableSeat t : floor1Available) {
                    cbTable.addItem(t.getTableNumber() + " - " + t.getName() + " (" + t.getCapacity() + " chỗ)");
                }
            }
            if (!floor2Available.isEmpty()) {
                cbTable.addItem("── Tầng 2 ──");
                for (TableSeat t : floor2Available) {
                    cbTable.addItem(t.getTableNumber() + " - " + t.getName() + " (" + t.getCapacity() + " chỗ)");
                }
            }
        }

        tablePanel.add(cbTable, BorderLayout.CENTER);
        mainPanel.add(tablePanel);

        // ── Date time ──
        JPanel datePanel = new JPanel(new GridLayout(0, 2, 8, 8));
        datePanel.setOpaque(false);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JTextField txtDateTime = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        txtDateTime.setFont(FONT_BODY);

        datePanel.add(new JLabel("Ngày giờ (dd-MM-yyyy HH:mm):"));
        datePanel.add(txtDateTime);
        mainPanel.add(datePanel);

        // ── Note ──
        JPanel notePanel = new JPanel(new GridLayout(0, 2, 8, 8));
        notePanel.setOpaque(false);
        notePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JTextField txtNote = new JTextField();
        txtNote.setFont(FONT_BODY);

        notePanel.add(new JLabel("Ghi chú:"));
        notePanel.add(txtNote);
        mainPanel.add(notePanel);

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Lưu", BTN_GREEN);
        JButton btnCancel = createButton("Hủy", BTN_RED);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            try {
                String customerName = null;
                Integer customerId = null;

                if (rbExisting.isSelected()) {
                    int custIdx = cbCustomer.getSelectedIndex();
                    if (custIdx <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Customer selectedCust = customers.get(custIdx - 1);
                    customerName = selectedCust.getName();
                    customerId = selectedCust.getId();
                } else {
                    String name = txtNewName.getText().trim();
                    String phone = txtNewPhone.getText().trim();
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Tên khách không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (phone.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Số điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    customerName = name;
                }

                // Parse selected table - find the actual table object
                int tableIdx = cbTable.getSelectedIndex();
                if (tableIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedStr = (String) cbTable.getSelectedItem();
                if (selectedStr == null || selectedStr.startsWith("──") || selectedStr.startsWith("Không có")) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một bàn hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Extract table number from string like "1 - Bàn 1 (4 chỗ) [✓]"
                int tableNumber;
                try {
                    tableNumber = Integer.parseInt(selectedStr.split(" - ")[0].trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi chọn bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Find the table object
                TableSeat selectedTable = allTables.stream()
                        .filter(t -> t.getTableNumber() == tableNumber)
                        .findFirst().orElse(null);
                if (selectedTable == null) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!"Trống".equals(selectedTable.getStatus())) {
                    JOptionPane.showMessageDialog(dialog, "Bàn này đang không trống!\nVui lòng chọn bàn có dấu ✓", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

                Reservation reservation = new Reservation(customerName, selectedTable.getTableNumber(), dateTime, "Đang đặt", note);
                reservation.setCustomerId(customerId);

                controller.addReservation(reservation);
                updateTableStatus(selectedTable.getTableNumber(), "Đang sử dụng");
                DataChangeEventBus.notifyChange();
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
        List<TableSeat> allTables = tableController.getAllTables();

        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(window instanceof Frame ? (Frame) window : null, "Sửa đặt bàn", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JComboBox<String> cbCustomer = new JComboBox<>();
        cbCustomer.addItem("-- Chọn khách hàng --");
        int selectedCustIdx = 0;
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            cbCustomer.addItem(c.getCode() + " - " + c.getName());
            if (r.getCustomerId() != null && c.getId() == r.getCustomerId()) {
                selectedCustIdx = i + 1;
            }
        }
        cbCustomer.setSelectedIndex(selectedCustIdx);
        cbCustomer.setFont(FONT_BODY);

        JComboBox<String> cbTable = new JComboBox<>();
        int selectedTableIdx = 0;
        for (int i = 0; i < allTables.size(); i++) {
            TableSeat t = allTables.get(i);
            cbTable.addItem(t.getTableNumber() + " - " + t.getName() + " (Tầng " + t.getFloor() + ", " + t.getCapacity() + " chỗ)");
            if (t.getTableNumber() == r.getTableNumber()) {
                selectedTableIdx = i;
            }
        }
        cbTable.setSelectedIndex(selectedTableIdx);
        cbTable.setFont(FONT_BODY);

        JTextField txtDateTime = new JTextField(r.getTime() != null ? r.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "");
        JTextField txtNote = new JTextField(r.getNote() != null ? r.getNote() : "");

        txtDateTime.setFont(FONT_BODY);
        txtNote.setFont(FONT_BODY);

        form.add(new JLabel("Khách hàng:"));  form.add(cbCustomer);
        form.add(new JLabel("Chọn bàn:"));     form.add(cbTable);
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
                if (custIdx <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Customer selectedCust = customers.get(custIdx - 1);

                int tableIdx = cbTable.getSelectedIndex();
                if (tableIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                TableSeat selectedTable = allTables.get(tableIdx);

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(txtDateTime.getText().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Ngày giờ không đúng định dạng dd-MM-yyyy HH:mm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String note = txtNote.getText().trim();

                int oldTableNumber = r.getTableNumber();
                r.setCustomerName(selectedCust.getName());
                r.setCustomerId(selectedCust.getId());
                r.setTableNumber(selectedTable.getTableNumber());
                r.setTime(dateTime);
                r.setNote(note);

                controller.updateReservation(r);
                if (oldTableNumber != selectedTable.getTableNumber()) {
                    updateTableStatus(oldTableNumber, "Trống");
                    updateTableStatus(selectedTable.getTableNumber(), "Đang sử dụng");
                }
                DataChangeEventBus.notifyChange();
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

        Reservation r = controller.getReservationById(id);
        if (r != null) {
            updateTableStatus(r.getTableNumber(), "Trống");
        }

        controller.deleteReservation(id);
        loadData();
    }

    /* ================= UPDATE TABLE STATUS ================= */
    private void updateTableStatus(int tableNumber, String status) {
        List<TableSeat> allTables = tableController.getAllTables();
        for (TableSeat t : allTables) {
            if (t.getTableNumber() == tableNumber) {
                t.setStatus(status);
                tableController.updateTable(t);
                break;
            }
        }
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
