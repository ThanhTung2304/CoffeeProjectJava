package org.example.view;

import org.example.controller.VoucherController;
import org.example.entity.Voucher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class VoucherManagementPanel extends JPanel {

    // ===== COLOR =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color BORDER = new Color(0xE2E8F0);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_AMBER = new Color(0xF59E0B);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_GRAY = new Color(0x64748B);

    // ===== CONTROLLER =====
    private final VoucherController controller = new VoucherController();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCount;

    public VoucherManagementPanel() {
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
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("🎟 Quản Lý Voucher");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        rowCount = new JLabel("0 voucher");
        rowCount.setForeground(new Color(0xCBD5E1));

        p.add(title, BorderLayout.WEST);
        p.add(rowCount, BorderLayout.EAST);

        return p;
    }

    // ===== CENTER =====
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 20, 20, 20));
        p.setBackground(BG);

        p.add(buildControl(), BorderLayout.NORTH);
        p.add(buildTable(), BorderLayout.CENTER);

        return p;
    }

    // ===== CONTROL BAR =====
    private JPanel buildControl() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        // search
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        txtSearch = new JTextField(18);
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Còn hiệu lực", "Hết hạn", "Đã sử dụng"});

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnSearch);

        // action
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Refresh", BTN_GRAY);
        JButton btnExport = createButton("↓ Excel", BTN_BLUE);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        right.add(btnExport);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // events
        btnSearch.addActionListener(e -> loadData());
        cbStatus.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e -> add());
        btnEdit.addActionListener(e -> edit());
        btnDelete.addActionListener(e -> delete());
        btnExport.addActionListener(e -> export());

        return bar;
    }

    // ===== TABLE =====
    private JScrollPane buildTable() {

        model = new DefaultTableModel(new String[]{
                "ID", "Mã", "Loại", "Giá trị",
                "Bắt đầu", "Kết thúc", "Trạng thái",
                "Giới hạn", "Đã dùng", "Ghi chú"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(ROW_SELECTED);

        // zebra
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean f, int r, int c) {

                super.getTableCellRendererComponent(t, v, sel, f, r, c);

                if (sel) setBackground(ROW_SELECTED);
                else setBackground(r % 2 == 0 ? ROW_ODD : ROW_EVEN);

                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        return sp;
    }

    // ===== LOAD DATA =====
    private void loadData() {
        model.setRowCount(0);

        List<Voucher> list = controller.getAll(
                txtSearch.getText(),
                cbStatus.getSelectedItem().toString()
        );

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

        rowCount.setText(list.size() + " voucher");
    }

    // ===== CRUD =====
    private void add() {
        Voucher v = form(null);
        if (v != null) {
            controller.add(v);
            loadData();
        }
    }

    private void edit() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        Voucher v = rowToVoucher(row);
        Voucher newV = form(v);

        if (newV != null) {
            newV.setId(v.getId());
            controller.update(newV);
            loadData();
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this,
                "Xóa voucher?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == 0) {
            controller.delete(id);
            loadData();
        }
    }

    // ===== FORM =====
    private Voucher form(Voucher v0) {

        JTextField code = new JTextField(v0 == null ? "" : v0.getCode());
        JTextField value = new JTextField(v0 == null ? "" : String.valueOf(v0.getDiscountValue()));

        JPanel p = new JPanel(new GridLayout(0,2,10,10));
        p.add(new JLabel("Mã")); p.add(code);
        p.add(new JLabel("Giá trị")); p.add(value);

        if (JOptionPane.showConfirmDialog(this, p,
                "Voucher", JOptionPane.OK_CANCEL_OPTION) == 0) {

            Voucher v = new Voucher();
            v.setCode(code.getText());
            v.setDiscountValue(Double.parseDouble(value.getText()));
            v.setStartDate(LocalDate.now());
            v.setEndDate(LocalDate.now().plusDays(10));
            v.setStatus("Còn hiệu lực");
            return v;
        }
        return null;
    }

    private Voucher rowToVoucher(int row) {
        Voucher v = new Voucher();
        v.setId((int) model.getValueAt(row, 0));
        v.setCode(model.getValueAt(row, 1).toString());
        return v;
    }

    // ===== EXPORT =====
    private void export() {
        try (Workbook wb = new XSSFWorkbook()) {

            Sheet s = wb.createSheet("Voucher");

            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = s.createRow(r);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    row.createCell(c).setCellValue(
                            String.valueOf(model.getValueAt(r,c))
                    );
                }
            }

            FileOutputStream fos = new FileOutputStream("voucher.xlsx");
            wb.write(fos);

            JOptionPane.showMessageDialog(this, "Xuất Excel OK");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== BUTTON STYLE =====
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        return btn;
    }
}