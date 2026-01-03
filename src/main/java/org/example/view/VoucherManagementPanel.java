package org.example.view;

import org.example.controller.VoucherController;
import org.example.entity.Voucher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ===== Apache POI (Excel) =====
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

        // ===== Nút chức năng =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExport = new JButton("Xuất Excel");

        btnAdd.setBackground(new Color(46, 204, 113));
        btnEdit.setBackground(new Color(241, 196, 15));
        btnDelete.setBackground(new Color(231, 76, 60));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnExport.setBackground(new Color(155, 89, 182));

        btnAdd.setForeground(Color.WHITE);
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setForeground(Color.WHITE);
        btnRefresh.setForeground(Color.WHITE);
        btnExport.setForeground(Color.WHITE);

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnExport);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(actionPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.PAGE_START);

        // ===== Bảng =====
        String[] columns = {
                "ID", "Mã", "Loại giảm", "Giá trị",
                "Bắt đầu", "Kết thúc", "Trạng thái",
                "Giới hạn", "Đã dùng", "Ghi chú"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
        btnExport.addActionListener(e -> exportToExcel());

        loadData();
    }

    // ===== Load data =====
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

    // ===== Xuất Excel =====
    private void exportToExcel() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("voucher.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Voucher");

            // ===== Style header =====
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header
            Row header = sheet.createRow(0);
            for (int c = 0; c < model.getColumnCount(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(model.getColumnName(c));
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    row.createCell(c).setCellValue(val == null ? "" : val.toString());
                }
            }

            for (int c = 0; c < model.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
            }

            try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {
                wb.write(fos);
            }

            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + ex.getMessage());
        }
    }

    // ===== CRUD =====
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
        if (JOptionPane.showConfirmDialog(this, "Xóa voucher này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData();
        }
    }

    // ===== Form =====
    private Voucher showVoucherForm(Voucher v0) {
        JTextField tfCode = new JTextField(v0 == null ? "" : v0.getCode());
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Phần trăm", "Số tiền"});
        JTextField tfValue = new JTextField(v0 == null ? "" : String.valueOf(v0.getDiscountValue()));
        JTextField tfStart = new JTextField(v0 == null ? "" : v0.getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        JTextField tfEnd = new JTextField(v0 == null ? "" : v0.getEndDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Còn hiệu lực", "Hết hạn", "Đã sử dụng"});
        JTextField tfLimit = new JTextField(v0 == null || v0.getUsageLimit() == null ? "" : v0.getUsageLimit().toString());
        JTextField tfUsed = new JTextField(v0 == null ? "0" : String.valueOf(v0.getUsedCount()));
        JTextField tfNote = new JTextField(v0 == null ? "" : v0.getNote());

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Mã:")); panel.add(tfCode);
        panel.add(new JLabel("Loại giảm:")); panel.add(cbType);
        panel.add(new JLabel("Giá trị:")); panel.add(tfValue);
        panel.add(new JLabel("Bắt đầu:")); panel.add(tfStart);
        panel.add(new JLabel("Kết thúc:")); panel.add(tfEnd);
        panel.add(new JLabel("Trạng thái:")); panel.add(cbStatus);
        panel.add(new JLabel("Giới hạn:")); panel.add(tfLimit);
        panel.add(new JLabel("Đã dùng:")); panel.add(tfUsed);
        panel.add(new JLabel("Ghi chú:")); panel.add(tfNote);

        if (JOptionPane.showConfirmDialog(this, panel,
                v0 == null ? "Thêm Voucher" : "Sửa Voucher",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Voucher v = new Voucher();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                v.setCode(tfCode.getText());
                v.setDiscountType((String) cbType.getSelectedItem());
                v.setDiscountValue(Double.parseDouble(tfValue.getText()));
                v.setStartDate(LocalDate.parse(tfStart.getText(), fmt));
                v.setEndDate(LocalDate.parse(tfEnd.getText(), fmt));
                v.setStatus((String) cbStatus.getSelectedItem());
                v.setUsageLimit(tfLimit.getText().isBlank() ? null : Integer.parseInt(tfLimit.getText()));
                v.setUsedCount(Integer.parseInt(tfUsed.getText()));
                v.setNote(tfNote.getText());
                return v;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
            }
        }
        return null;
    }

    private Voucher tableRowToVoucher(int row) {
        Voucher v = new Voucher();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        v.setId((int) model.getValueAt(row, 0));
        v.setCode(model.getValueAt(row, 1).toString());
        v.setDiscountType(model.getValueAt(row, 2).toString());
        v.setDiscountValue(Double.parseDouble(model.getValueAt(row, 3).toString()));
        v.setStartDate(LocalDate.parse(model.getValueAt(row, 4).toString(), fmt));
        v.setEndDate(LocalDate.parse(model.getValueAt(row, 5).toString(), fmt));
        v.setStatus(model.getValueAt(row, 6).toString());
        v.setUsageLimit(model.getValueAt(row, 7) == null ? null : Integer.parseInt(model.getValueAt(row, 7).toString()));
        v.setUsedCount(Integer.parseInt(model.getValueAt(row, 8).toString()));
        v.setNote(model.getValueAt(row, 9).toString());
        return v;
    }
}
