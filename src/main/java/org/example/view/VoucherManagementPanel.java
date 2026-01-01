package org.example.view;

import org.example.controller.VoucherController;
import org.example.entity.Voucher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VoucherManagementPanel extends JPanel {

    private final VoucherController controller = new VoucherController();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public VoucherManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Tiêu đề =====
        JLabel title = new JLabel("QUẢN LÝ VOUCHER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Thanh tìm kiếm =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(18);
        JButton btnSearch = new JButton("Tìm");
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Còn hiệu lực", "Hết hạn", "Đã sử dụng"});

        topPanel.add(new JLabel("Tìm mã / ghi chú:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);

        // ===== Thanh nút chức năng (ở trên bảng) =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(46, 204, 113)); // xanh lá
        btnAdd.setForeground(Color.WHITE);

        JButton btnEdit = new JButton("Sửa");
        btnEdit.setBackground(new Color(241, 196, 15)); // vàng cam
        btnEdit.setForeground(Color.WHITE);

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(231, 76, 60)); // đỏ
        btnDelete.setForeground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219)); // xanh dương
        btnRefresh.setForeground(Color.WHITE);

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // Gom thanh tìm kiếm + nút chức năng vào một panel trên cùng
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(actionPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.PAGE_START);

        // ===== Bảng dữ liệu =====
        String[] columns = {"ID", "Mã", "Loại giảm", "Giá trị", "Bắt đầu", "Kết thúc", "Trạng thái", "Giới hạn", "Đã dùng", "Ghi chú"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Sự kiện =====
        btnSearch.addActionListener(e -> loadData());
        cbStatus.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());

        loadData();
    }

    // ===== Các hàm xử lý CRUD =====
    private void loadData() {
        String keyword = txtSearch.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        List<Voucher> list = controller.getAll(keyword, status);

        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Voucher v : list) {
            model.addRow(new Object[]{
                    v.getId(),
                    v.getCode(),
                    v.getDiscountType(),
                    v.getDiscountValue(),
                    v.getStartDate().format(fmt),
                    v.getEndDate().format(fmt),
                    v.getStatus(),
                    v.getUsageLimit(),
                    v.getUsedCount(),
                    v.getNote()
            });
        }
    }


    private void showAddDialog() {
        Voucher v = showVoucherForm(null);
        if (v != null) {
            controller.add(v);
            loadData();
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa!");
            return;
        }
        Voucher current = tableRowToVoucher(row);
        Voucher edited = showVoucherForm(current);
        if (edited != null) {
            edited.setId(current.getId());
            controller.update(edited);
            loadData();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa voucher này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData();
        }
    }

    private Voucher showVoucherForm(Voucher v0) {
        JTextField tfCode = new JTextField(v0 == null ? "" : v0.getCode());
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Phần trăm", "Số tiền"});
        if (v0 != null) cbType.setSelectedItem(v0.getDiscountType());
        JTextField tfValue = new JTextField(v0 == null ? "" : String.valueOf(v0.getDiscountValue()));
        JTextField tfStart = new JTextField(v0 == null ? "" : v0.getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        JTextField tfEnd = new JTextField(v0 == null ? "" : v0.getEndDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Còn hiệu lực", "Hết hạn", "Đã sử dụng"});
        if (v0 != null) cbStatus.setSelectedItem(v0.getStatus());
        JTextField tfLimit = new JTextField(v0 == null || v0.getUsageLimit() == null ? "" : String.valueOf(v0.getUsageLimit()));
        JTextField tfUsed = new JTextField(v0 == null || v0.getUsedCount() == null ? "0" : String.valueOf(v0.getUsedCount()));
        JTextField tfNote = new JTextField(v0 == null ? "" : v0.getNote());

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Mã:")); panel.add(tfCode);
        panel.add(new JLabel("Loại giảm:")); panel.add(cbType);
        panel.add(new JLabel("Giá trị:")); panel.add(tfValue);
        panel.add(new JLabel("Bắt đầu (dd-MM-yyyy):")); panel.add(tfStart);
        panel.add(new JLabel("Kết thúc (dd-MM-yyyy):")); panel.add(tfEnd);
        panel.add(new JLabel("Trạng thái:")); panel.add(cbStatus);
        panel.add(new JLabel("Giới hạn lượt:")); panel.add(tfLimit);
        panel.add(new JLabel("Đã dùng:")); panel.add(tfUsed);
        panel.add(new JLabel("Ghi chú:")); panel.add(tfNote);

        int result = JOptionPane.showConfirmDialog(this, panel, v0 == null ? "Thêm Voucher" : "Sửa Voucher",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Voucher v = new Voucher();
                v.setCode(tfCode.getText().trim());
                v.setDiscountType((String) cbType.getSelectedItem());
                v.setDiscountValue(Double.parseDouble(tfValue.getText().trim()));
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                v.setStartDate(LocalDate.parse(tfStart.getText().trim(), fmt));
                v.setEndDate(LocalDate.parse(tfEnd.getText().trim(), fmt));
                v.setStatus((String) cbStatus.getSelectedItem());
                String limitStr = tfLimit.getText().trim();
                v.setUsageLimit(limitStr.isBlank() ? null : Integer.parseInt(limitStr));
                String usedStr = tfUsed.getText().trim();
                v.setUsedCount(usedStr.isBlank() ? 0 : Integer.parseInt(usedStr));
                v.setNote(tfNote.getText().trim());
                return v;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    private Voucher tableRowToVoucher(int row) {
        Voucher v = new Voucher();
        v.setId((int) model.getValueAt(row, 0));
        v.setCode(String.valueOf(model.getValueAt(row, 1)));
        v.setDiscountType(String.valueOf(model.getValueAt(row, 2)));
        v.setDiscountValue(Double.parseDouble(String.valueOf(model.getValueAt(row, 3))));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        v.setStartDate(LocalDate.parse(String.valueOf(model.getValueAt(row, 4)), fmt));
        v.setEndDate(LocalDate.parse(String.valueOf(model.getValueAt(row, 5)), fmt));
        v.setStatus(String.valueOf(model.getValueAt(row, 6)));
        Object limit = model.getValueAt(row, 7);
        v.setUsageLimit(limit == null ? null : Integer.parseInt(String.valueOf(limit)));
        Object used = model.getValueAt(row, 8);
        v.setUsedCount(used == null ? 0 : Integer.parseInt(String.valueOf(used)));
        v.setNote(String.valueOf(model.getValueAt(row, 9)));
        return v;
    }
}