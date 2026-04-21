package org.example.view;

import org.example.controller.TableController;
import org.example.entity.TableSeat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TableManagementPanel extends JPanel {

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
    private static final Color BTN_SLATE = new Color(0x64748B);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCount;

    private final TableController controller = new TableController();

    public TableManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadData();
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("🍽 Quản Lý Bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        rowCount = new JLabel("0 bàn");
        rowCount.setForeground(Color.WHITE);

        p.add(title, BorderLayout.WEST);
        p.add(rowCount, BorderLayout.EAST);
        return p;
    }

    // ===== CENTER =====
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 20, 20, 20));
        p.setBackground(BG);

        p.add(buildControlBar(), BorderLayout.NORTH);
        p.add(buildTable(), BorderLayout.CENTER);

        return p;
    }

    // ===== CONTROL =====
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        // LEFT: search
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(180, 34));

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang sử dụng", "Đặt trước"});

        JButton btnSearch = createButton("🔍", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnSearch);

        // RIGHT: buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻", BTN_SLATE);
        JButton btnExport = createButton("↓ Excel", BTN_BLUE);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        right.add(btnExport);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // EVENTS
        btnSearch.addActionListener(e -> filterData());
        txtSearch.addActionListener(e -> filterData());
        cbStatus.addActionListener(e -> filterData());

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteTable());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        return bar;
    }

    // ===== TABLE =====
    private JScrollPane buildTable() {
        model = new DefaultTableModel(
                new String[]{"Số bàn", "Tên bàn", "Sức chứa", "Trạng thái", "Ghi chú"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setAutoCreateRowSorter(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(ROW_SELECTED);

        // RENDERER (badge trạng thái)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                if (col == 3 && value != null) {
                    JLabel lb = new JLabel(value.toString(), CENTER);
                    lb.setOpaque(true);

                    switch (value.toString()) {
                        case "Trống" -> lb.setBackground(new Color(0xDCFCE7));
                        case "Đang sử dụng" -> lb.setBackground(new Color(0xFEE2E2));
                        case "Đặt trước" -> lb.setBackground(new Color(0xFEF9C3));
                    }
                    return lb;
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (isSelected) setBackground(ROW_SELECTED);
                else setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);

                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0x334155));
        th.setForeground(Color.WHITE);

        return new JScrollPane(table);
    }

    // ===== LOAD =====
    private void loadData() {
        model.setRowCount(0);

        List<TableSeat> list = controller.getAllTables();
        for (TableSeat t : list) {
            model.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }

        rowCount.setText(list.size() + " bàn");
    }

    // ===== FILTER =====
    private void filterData() {
        String keyword = txtSearch.getText().toLowerCase();
        String status = cbStatus.getSelectedItem().toString();

        List<TableSeat> list = controller.getAllTables().stream()
                .filter(t ->
                        (keyword.isEmpty() || t.getName().toLowerCase().contains(keyword))
                                && ("Tất cả".equals(status) || t.getStatus().equals(status))
                ).collect(Collectors.toList());

        model.setRowCount(0);
        for (TableSeat t : list) {
            model.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }

        rowCount.setText(list.size() + " bàn");
    }

    // ===== CRUD =====
    private void showAddDialog() {
        JOptionPane.showMessageDialog(this, "Dùng lại form cũ của bạn");
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int modelRow = table.convertRowIndexToModel(row);
        JOptionPane.showMessageDialog(this, "Edit row: " + modelRow);
    }

    private void deleteTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        controller.deleteTable(id);
        loadData();
    }

    // ===== EXPORT =====
    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("table.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {

            Sheet sheet = wb.createSheet("Table");

            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.createRow(0).createCell(i).setCellValue(model.getColumnName(i));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                Row r = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    r.createCell(j).setCellValue(model.getValueAt(i, j).toString());
                }
            }

            wb.write(fos);
            JOptionPane.showMessageDialog(this, "Xuất Excel OK");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi export");
        }
    }

    // ===== BUTTON =====
    private JButton createButton(String text, Color c) {
        JButton b = new JButton(text);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}