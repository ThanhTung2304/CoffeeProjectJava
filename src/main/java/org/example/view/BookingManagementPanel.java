package org.example.view;

import org.example.controller.ReservationController;
import org.example.entity.Reservation;

// ===== Swing / AWT =====
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

// ===== Apache POI =====
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// ===== Java =====
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingManagementPanel extends JPanel {

    private final ReservationController controller = new ReservationController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public BookingManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Tiêu đề =====
        JLabel title = new JLabel("QUẢN LÝ ĐẶT BÀN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Thanh tìm kiếm =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        searchPanel.setBackground(new Color(245, 245, 245));

        searchField = new JTextField(15);
        JButton btnSearch = new JButton("🔍 Tìm");
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);

        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang đặt", "Hoàn thành", "Hủy"});

        searchPanel.add(new JLabel("Tìm:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(30));
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusFilter);

        add(searchPanel, BorderLayout.PAGE_START);

        // ===== Thanh nút =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton btnAdd = createButton("Thêm", new Color(0, 153, 76));
        JButton btnEdit = createButton("Sửa", new Color(255, 153, 0));
        JButton btnDelete = createButton("Xóa", new Color(204, 0, 0));
        JButton btnRefresh = createButton("Refresh", new Color(0, 102, 204));
        JButton btnExport = createButton("Xuất Excel", new Color(0, 153, 153));

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteBooking());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExport);

        // ===== Bảng =====
        String[] columns = {"ID", "Tên khách hàng", "Bàn số", "Thời gian", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        loadData();
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> list = controller.getAllReservations();
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getCustomerName(),
                    r.getTableNumber(),
                    r.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    r.getStatus(),
                    r.getNote()
            });
        }
    }

    private void applyFilters() {
        String keyword = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();

        tableModel.setRowCount(0);
        for (Reservation r : controller.getAllReservations()) {
            boolean match = r.getCustomerName().toLowerCase().contains(keyword);
            boolean matchStatus = status.equals("Tất cả") || status.equalsIgnoreCase(r.getStatus());

            if (match && matchStatus) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getCustomerName(),
                        r.getTableNumber(),
                        r.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        r.getStatus(),
                        r.getNote()
                });
            }
        }
    }

    // ===== XUẤT EXCEL (ĐÃ SỬA CHUẨN) =====
    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("DanhSachDatBan.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Đặt bàn");

            // Header
            Row header = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            excelFont.setBold(true);
            headerStyle.setFont(excelFont);

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    row.createCell(c).setCellValue(val == null ? "" : val.toString());
                }
            }

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
            workbook.write(fos);
            fos.close();

            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Xuất Excel thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===== CRUD =====
    private void showAddDialog() {
        JTextField customerNameField = new JTextField();
        JTextField tableNumberField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Tên khách hàng:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Số bàn:"));
        panel.add(tableNumberField);
        panel.add(new JLabel("Thời gian (dd-MM-yyyy):"));
        panel.add(timeField);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Thêm đặt bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Reservation r = new Reservation();
                r.setCustomerName(customerNameField.getText());
                r.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                r.setTime(LocalDate.parse(timeField.getText(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
                r.setStatus(statusField.getText());
                r.setNote(noteField.getText());
                controller.addReservation(r);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
            return;
        }

        Reservation r = controller.getAllReservations().get(row);

        JTextField customerNameField = new JTextField(r.getCustomerName());
        JTextField tableNumberField = new JTextField(String.valueOf(r.getTableNumber()));
        JTextField timeField = new JTextField(
                r.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        JTextField statusField = new JTextField(r.getStatus());
        JTextField noteField = new JTextField(r.getNote());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Tên khách hàng:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Số bàn:"));
        panel.add(tableNumberField);
        panel.add(new JLabel("Thời gian (dd-MM-yyyy):"));
        panel.add(timeField);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Sửa đặt bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                r.setCustomerName(customerNameField.getText());
                r.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                r.setTime(LocalDate.parse(timeField.getText(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
                r.setStatus(statusField.getText());
                r.setNote(noteField.getText());
                controller.updateReservation(r);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBooking() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xoá!");
            return;
        }
        Reservation r = controller.getAllReservations().get(row);
        controller.deleteReservation(r.getId());
        loadData();
    }
}
