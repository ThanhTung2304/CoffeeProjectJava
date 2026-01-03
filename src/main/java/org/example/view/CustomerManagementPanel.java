package org.example.view;

<<<<<<< HEAD
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
=======
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
import org.example.controller.CustomerController;
import org.example.entity.Customer;

import javax.swing.*;
<<<<<<< HEAD
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import org.apache.poi.ss.usermodel.*;
=======
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.example.view.MainFrame;



>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

public class CustomerManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
<<<<<<< HEAD

    private JTextField txtSearch;
    private JComboBox<String> cbType;
=======
    private JTextField txtSearch;
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8

    private final CustomerController controller = new CustomerController();

    public CustomerManagementPanel() {
<<<<<<< HEAD
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        /* ===== TITLE ===== */
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(
                new java.awt.Font("Arial", java.awt.Font.BOLD, 20)
        );

        /* ===== SEARCH + FILTER ===== */
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");

        cbType = new JComboBox<>(new String[]{
                "Tất cả"
        });

        filterPanel.add(new JLabel("Tìm:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Loại KH:"));
        filterPanel.add(cbType);

        /* ===== BUTTONS ===== */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        JButton btnExport = createButton("Xuất Excel", new Color(52, 152, 219));

        buttonPanel.add(btnExport);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        /* ===== TOP ===== */
        JPanel top = new JPanel(new BorderLayout());
        top.add(lblTitle, BorderLayout.NORTH);
        top.add(filterPanel, BorderLayout.CENTER);
        top.add(buttonPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new Object[]{
                        "STT", "Tên KH", "SĐT", "Email",
                        "Điểm", "Ngày tạo", "Cập nhật"
                },
                0
        );

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbType.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e ->
                new CustomerForm(null, this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnExport.addActionListener(e -> exportToExcel());

    }

    /* ===== LOAD DATA ===== */
    private void loadData() {
        model.setRowCount(0);

        String keyword = txtSearch.getText().toLowerCase();

        List<Customer> list = controller.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int stt = 1;
        for (Customer c : list) {

            boolean match =
                    keyword.isBlank() ||
                            c.getName().toLowerCase().contains(keyword) ||
                            (c.getPhone() != null && c.getPhone().contains(keyword));

            if (match) {
                model.addRow(new Object[]{
                        stt++,
                        c.getName(),
                        c.getPhone(),
                        c.getEmail(),
                        c.getPoints(),
                        c.getCreatedTime() == null ? "" : c.getCreatedTime().format(fmt),
                        c.getUpdateTime() == null ? "" : c.getUpdateTime().format(fmt)
                });
=======
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16,16,16,16));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0,0,16,0));

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");

        searchPanel.add(new JLabel("Tìm (Tên / SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // ===== BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,10));
        actionPanel.setOpaque(false);

        JButton btnAdd = createButton("Thêm", new Color(46,204,113));
        JButton btnEdit = createButton("Sửa", new Color(241,196,15));
        JButton btnDelete = createButton("Xóa", new Color(231,76,60));
        JButton btnRefresh = createButton("Refresh", new Color(149,165,166));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // ===== TABLE =====
        String[] columns = {
                "ID","Tên","SĐT","Email","Điểm","Ngày tạo"
        };

        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);

        // ===== LAYOUT =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(searchPanel, BorderLayout.CENTER);
        header.add(actionPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ===== LOAD DATA =====
        loadData("");

        // ===== EVENTS =====
        btnSearch.addActionListener(e ->
                loadData(txtSearch.getText().trim()));

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        btnAdd.addActionListener(e -> showAddDialog());

        btnEdit.addActionListener(e -> showEditDialog());

        btnDelete.addActionListener(e -> deleteCustomer());

        /*table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row == -1) return;

                    int customerId = (int) model.getValueAt(row, 0);
                    String name = model.getValueAt(row, 1).toString();

                    Customer c = new Customer();
                    c.setId(customerId);
                    c.setName(name);

                    MainFrame mainFrame =
                            (MainFrame) SwingUtilities.getWindowAncestor(CustomerManagementPanel.this);
                    if (!"ADMIN".equals(mainFrame.getRole())) {
                        JOptionPane.showMessageDialog(
                                CustomerManagementPanel.this,
                                "Bạn không có quyền xem lịch sử đơn hàng!",
                                "Từ chối truy cập",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    mainFrame.openOrderHistory(c);

                }
            }
        });*/

    }


    // ================= LOAD DATA =================
    private void loadData(String keyword) {
        model.setRowCount(0);
        List<Customer> list = controller.getAll();

        for (Customer c : list) {
            if (!keyword.isEmpty()) {
                String key = keyword.toLowerCase();
                if (!c.getName().toLowerCase().contains(key)
                        && !c.getPhone().contains(key)) {
                    continue;
                }
            }

            model.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getTotalPoint(),
                    c.getCreatedTime()
            });
        }
    }

    // ================= ADD =================
    private void showAddDialog() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();

        Object[] form = {
                "Tên:", name,
                "SĐT:", phone,
                "Email:", email
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form, "Thêm khách hàng",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            try {
                controller.add(
                        name.getText().trim(),
                        phone.getText().trim(),
                        email.getText().trim()
                );
                loadData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
            }
        }
    }

<<<<<<< HEAD
    private void editCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        Customer customer = controller.getAll().get(row);
        new CustomerForm(customer, this::loadData).setVisible(true);
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khách hàng này?",
=======
    // ================= EDIT =================
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng cần sửa");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        JTextField name = new JTextField(model.getValueAt(row,1).toString());
        JTextField phone = new JTextField(model.getValueAt(row,2).toString());
        JTextField email = new JTextField(model.getValueAt(row,3).toString());
        JTextField point = new JTextField(model.getValueAt(row,4).toString());

        Object[] form = {
                "Tên:", name,
                "SĐT:", phone,
                "Email:", email,
                "Điểm:", point
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form, "Sửa khách hàng",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            Customer c = new Customer();
            c.setId(id);
            c.setName(name.getText().trim());
            c.setPhone(phone.getText().trim());
            c.setEmail(email.getText().trim());
            c.setTotalPoint(Integer.parseInt(point.getText().trim()));

            controller.update(c);
            loadData("");
        }
    }

    // ================= DELETE =================
    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng cần xóa");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        int rs = JOptionPane.showConfirmDialog(
                this,
                "Bạn chắc chắn muốn xóa khách hàng này?",
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

<<<<<<< HEAD
        if (confirm == JOptionPane.YES_OPTION) {
            int id = controller.getAll().get(row).getId();
            controller.delete(id);
            loadData();
        }
    }

    private void exportToExcel() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File("customer.xlsx"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (
                Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(file)
        ) {

            Sheet sheet = workbook.createSheet("Customers");

            /* ================= HEADER STYLE ================= */
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont(); // ⚠️ Font của POI
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            /* ================= HEADER ================= */
            Row headerRow = sheet.createRow(0);

            String[] columns = {
                    "STT", "Tên KH", "SĐT", "Email",
                    "Điểm", "Ngày tạo", "Cập nhật"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            /* ================= DATA ================= */
            List<Customer> customers = controller.getAll();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowIndex = 1;
            int stt = 1;

            for (Customer c : customers) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(
                        c.getName() == null ? "" : c.getName()
                );
                row.createCell(2).setCellValue(
                        c.getPhone() == null ? "" : c.getPhone()
                );
                row.createCell(3).setCellValue(
                        c.getEmail() == null ? "" : c.getEmail()
                );
                row.createCell(4).setCellValue(c.getPoints());
                row.createCell(5).setCellValue(
                        c.getCreatedTime() == null ? "" : c.getCreatedTime().format(formatter)
                );
                row.createCell(6).setCellValue(
                        c.getUpdateTime() == null ? "" : c.getUpdateTime().format(formatter)
                );
            }

            /* ================= AUTO SIZE ================= */
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
=======
        if (rs == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData("");
        }
    }

    // ================= BUTTON STYLE =================
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120,36));
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        btn.setFocusPainted(false);
        return btn;
    }
}
