package org.example.view;

import org.example.controller.EmployeeController;
import org.example.entity.Employee;
import org.example.view.EmployeeForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JComboBox<String> cbPosition;

    private final EmployeeController controller = new EmployeeController();

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        /* ===== TITLE ===== */
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        /* ===== SEARCH + FILTER ===== */
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");

        cbPosition = new JComboBox<>(new String[]{
                "Tất cả", "Staff"
        });

        filterPanel.add(new JLabel("Tìm:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Vị trí:"));
        filterPanel.add(cbPosition);

        /* ===== BUTTONS ===== */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

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
                        "STT", "Tên", "SĐT", "Vai trò",
                        "Username", "Ngày tạo", "Cập nhật"
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
            cbPosition.setSelectedIndex(0);
            loadData();
        });

        btnAdd.addActionListener(e ->new EmployeeForm(null, this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
    }

    /* ===== LOAD DATA ===== */
    private void loadData() {
            model.setRowCount(0);

            String keyword = txtSearch.getText().toLowerCase();
            String positionFilter = cbPosition.getSelectedItem().toString();

            List<Employee> list = controller.getAll();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int stt = 1;
            for (Employee e : list) {

                boolean matchName =
                        keyword.isBlank() ||
                                e.getName().toLowerCase().contains(keyword);

                boolean matchPosition =
                        positionFilter.equals("Tất cả") ||
                                e.getPosition().toLowerCase().startsWith(positionFilter.toLowerCase());

                if (matchName && matchPosition) {
                    model.addRow(new Object[]{
                            stt++,
                            e.getName(),
                            e.getPhone(),
                            e.getPosition(),// đã chứa username
                            e.getUsername(),
                            e.getCreatedTime() == null ? "" : e.getCreatedTime().format(fmt),
                            e.getUpdateTime() == null ? "" : e.getUpdateTime().format(fmt)
                    });
                }
            }
        }


        private void editEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) return;


        Employee emp = controller.getAll().get(row);

        new EmployeeForm(emp, this::loadData).setVisible(true);
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhân viên này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = controller.getAll().get(row).getId();
            controller.delete(id);
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
}