package org.example.view;

import org.example.controller.ShiftController;
import org.example.entity.Shift;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShiftManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ShiftController controller = new ShiftController();

    public ShiftManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        /* ===== TITLE ===== */
        JLabel lblTitle = new JLabel("QUẢN LÝ CA LÀM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        /* ===== BUTTONS ===== */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        JButton btnExport = createButton("Xuất Excel", new Color(52, 152, 219));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        buttonPanel.add(btnExport);

        JPanel top = new JPanel(new BorderLayout());
        top.add(lblTitle, BorderLayout.NORTH);
        top.add(buttonPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new Object[]{"STT", "Tên ca", "Bắt đầu", "Kết thúc", "Ngày tạo", "Cập nhật"},
                0
        );

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnAdd.addActionListener(e ->
                new ShiftForm(null, this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editShift());
        btnDelete.addActionListener(e -> deleteShift());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

    }

    private void loadData() {
        model.setRowCount(0);

        List<Shift> list = controller.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int stt = 1;
        for (Shift s : list) {
            model.addRow(new Object[]{
                    stt++,
                    s.getName(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getCreatedTime() == null ? "" : s.getCreatedTime().format(fmt),
                    s.getUpdateTime() == null ? "" : s.getUpdateTime().format(fmt)
            });
        }
    }

    private void editShift() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        Shift shift = controller.getAll().get(row);
        new ShiftForm(shift, this::loadData).setVisible(true);
    }

    private void deleteShift() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa ca làm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = controller.getAll().get(row).getId();
            controller.delete(id);
            loadData();
        }
    }
    private void exportToExcel() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File("shift.xlsx"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (
                org.apache.poi.ss.usermodel.Workbook workbook =
                        new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(file)
        ) {

            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.createSheet("Shifts");

            /* ===== HEADER STYLE ===== */
            org.apache.poi.ss.usermodel.CellStyle headerStyle =
                    workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font headerFont =
                    workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            /* ===== HEADER ===== */
            org.apache.poi.ss.usermodel.Row headerRow =
                    sheet.createRow(0);

            String[] columns = {
                    "STT", "Tên ca", "Bắt đầu", "Kết thúc",
                    "Ngày tạo", "Cập nhật"
            };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell =
                        headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            /* ===== DATA ===== */
            List<Shift> list = controller.getAll();
            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowIndex = 1;
            int stt = 1;

            for (Shift s : list) {
                org.apache.poi.ss.usermodel.Row row =
                        sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(
                        s.getName() == null ? "" : s.getName()
                );
                row.createCell(2).setCellValue(
                        s.getStartTime() == null ? "" : s.getStartTime().toString()
                );
                row.createCell(3).setCellValue(
                        s.getEndTime() == null ? "" : s.getEndTime().toString()
                );
                row.createCell(4).setCellValue(
                        s.getCreatedTime() == null ? "" : s.getCreatedTime().format(fmt)
                );
                row.createCell(5).setCellValue(
                        s.getUpdateTime() == null ? "" : s.getUpdateTime().format(fmt)
                );
            }

            /* ===== AUTO SIZE ===== */
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);

            JOptionPane.showMessageDialog(
                    this,
                    "Xuất Excel thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Xuất Excel thất bại: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}
