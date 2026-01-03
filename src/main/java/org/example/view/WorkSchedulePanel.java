package org.example.view;

import org.example.controller.WorkScheduleController;
import org.example.entity.WorkSchedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkSchedulePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private final WorkScheduleController controller = new WorkScheduleController();

    public WorkSchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        JLabel title = new JLabel("QUẢN LÝ LỊCH LÀM VIỆC");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        JButton btnExport = createButton("Xuất Excel", new Color(52, 152, 219));

        btnAdd.addActionListener(e ->
                        new EmployeeShiftForm(this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> edit());

        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> delete());

        buttonPanel.add(btnExport);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(buttonPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "STT", "Nhân viên", "Ca làm", "Bắt đầu", "Kết thúc", "Ngày làm", "Ngày đăng ký"
        }, 0);

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadData());

        btnDelete.addActionListener(e -> delete());
        btnExport.addActionListener(e -> exportToExcel());

    }

    private void loadData() {
        model.setRowCount(0);

        List<WorkSchedule> list = controller.getAll();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        DateTimeFormatter regFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int stt = 1;
        for (WorkSchedule ws : list) {
            model.addRow(new Object[]{
                    stt++,
                    ws.getEmployeeName(),
                    ws.getShiftName(),
                    ws.getStartTime().format(timeFmt),
                    ws.getEndTime().format(timeFmt),
                    ws.getWorkDate().format(dateFmt),
                    ws.getRegisterDate() == null
                            ? ""
                            : ws.getRegisterDate().format(regFmt)
            });
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        WorkSchedule ws = controller.getAll().get(row);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa lịch làm việc này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(
                    ws.getEmployeeId(),
                    ws.getShiftId(),
                    ws.getWorkDate().toString()
            );
            loadData();
        }
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
    private void edit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng để sửa");
            return;
        }

        WorkSchedule ws = controller.getAll().get(row);

        new EmployeeShiftEditForm(ws, this::loadData).setVisible(true);
    }
    private void exportToExcel() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File("work_schedule.xlsx"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (
                org.apache.poi.ss.usermodel.Workbook workbook =
                        new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(file)
        ) {

            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.createSheet("Work Schedule");

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
                    "STT",
                    "Nhân viên",
                    "Ca làm",
                    "Bắt đầu",
                    "Kết thúc",
                    "Ngày làm",
                    "Ngày đăng ký"
            };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell =
                        headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            /* ===== DATA ===== */
            List<WorkSchedule> list = controller.getAll();

            DateTimeFormatter timeFmt =
                    DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter regFmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            int rowIndex = 1;
            int stt = 1;

            for (WorkSchedule ws : list) {
                org.apache.poi.ss.usermodel.Row row =
                        sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(ws.getEmployeeName());
                row.createCell(2).setCellValue(ws.getShiftName());
                row.createCell(3).setCellValue(
                        ws.getStartTime().format(timeFmt)
                );
                row.createCell(4).setCellValue(
                        ws.getEndTime().format(timeFmt)
                );
                row.createCell(5).setCellValue(
                        ws.getWorkDate().format(dateFmt)
                );
                row.createCell(6).setCellValue(
                        ws.getRegisterDate() == null
                                ? ""
                                : ws.getRegisterDate().format(regFmt)
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


}
