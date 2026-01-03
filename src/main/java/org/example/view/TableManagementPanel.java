package org.example.view;

import org.example.controller.TableController;
import org.example.entity.TableSeat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

// ===== Apache POI =====
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TableManagementPanel extends JPanel {

    private final TableController controller = new TableController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public TableManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Tiêu đề =====
        JLabel title = new JLabel("QUẢN LÝ BÀN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Tìm kiếm =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm:"));

        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        searchPanel.add(btnSearch);

        searchPanel.add(new JLabel("Trạng thái:"));
        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang sử dụng", "Đặt trước"});
        statusFilter.addActionListener(e -> filterData());
        searchPanel.add(statusFilter);

        // ===== Nút chức năng =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExport = new JButton("Xuất Excel");

        btnAdd.setBackground(new Color(0, 153, 0));
        btnEdit.setBackground(new Color(255, 153, 0));
        btnDelete.setBackground(new Color(204, 0, 0));
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnExport.setBackground(new Color(155, 89, 182));

        btnAdd.setForeground(Color.WHITE);
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setForeground(Color.WHITE);
        btnRefresh.setForeground(Color.WHITE);
        btnExport.setForeground(Color.WHITE);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteTable());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExport);

        // ===== Bảng =====
        String[] columns = {"Số bàn", "Tên bàn", "Sức chứa", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadData();
    }

    // ===== Load dữ liệu =====
    private void loadData() {
        tableModel.setRowCount(0);
        List<TableSeat> tables = controller.getAllTables();
        for (TableSeat t : tables) {
            tableModel.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }
    }

    private void filterData() {
        String keyword = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();

        List<TableSeat> tables = controller.getAllTables().stream()
                .filter(t ->
                        (keyword.isEmpty()
                                || String.valueOf(t.getTableNumber()).contains(keyword)
                                || (t.getNote() != null && t.getNote().toLowerCase().contains(keyword)))
                                && ("Tất cả".equals(status) || t.getStatus().equals(status))
                )
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (TableSeat t : tables) {
            tableModel.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }
    }

    // ===== XUẤT EXCEL (ĐÃ FIX HOÀN CHỈNH) =====
    private void exportToExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File("DanhSachBan.xlsx"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sách bàn");

            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(tableModel.getColumnName(c));
                cell.setCellStyle(headerStyle);
            }

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    row.createCell(c).setCellValue(val == null ? "" : val.toString());
                }
            }

            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thành công!\n" + file.getAbsolutePath());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi xuất Excel!\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ===== CRUD =====
    private void showAddDialog() {
        JTextField numberField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Số bàn:")); panel.add(numberField);
        panel.add(new JLabel("Tên bàn:")); panel.add(nameField);
        panel.add(new JLabel("Sức chứa:")); panel.add(capacityField);
        panel.add(new JLabel("Trạng thái:")); panel.add(statusField);
        panel.add(new JLabel("Ghi chú:")); panel.add(noteField);

        if (JOptionPane.showConfirmDialog(this, panel,
                "Thêm bàn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                TableSeat t = new TableSeat();
                t.setTableNumber(Integer.parseInt(numberField.getText()));
                t.setName(nameField.getText());
                t.setCapacity(Integer.parseInt(capacityField.getText()));
                t.setStatus(statusField.getText());
                t.setNote(noteField.getText());
                controller.addTable(t);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!");
            }
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
            return;
        }

        int tableNumber = (int) tableModel.getValueAt(row, 0);
        TableSeat t = controller.getAllTables().stream()
                .filter(tb -> tb.getTableNumber() == tableNumber)
                .findFirst().orElse(null);
        if (t == null) return;

        JTextField nameField = new JTextField(t.getName());
        JTextField capacityField = new JTextField(String.valueOf(t.getCapacity()));
        JTextField statusField = new JTextField(t.getStatus());
        JTextField noteField = new JTextField(t.getNote());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Tên bàn:")); panel.add(nameField);
        panel.add(new JLabel("Sức chứa:")); panel.add(capacityField);
        panel.add(new JLabel("Trạng thái:")); panel.add(statusField);
        panel.add(new JLabel("Ghi chú:")); panel.add(noteField);

        if (JOptionPane.showConfirmDialog(this, panel,
                "Sửa bàn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                t.setName(nameField.getText());
                t.setCapacity(Integer.parseInt(capacityField.getText()));
                t.setStatus(statusField.getText());
                t.setNote(noteField.getText());
                controller.updateTable(t);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!");
            }
        }
    }

    private void deleteTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xoá!");
            return;
        }
        int tableNumber = (int) tableModel.getValueAt(row, 0);
        controller.deleteTable(tableNumber);
        loadData();
    }
}
