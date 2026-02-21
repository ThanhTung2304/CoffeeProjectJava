package org.example.view;

import org.example.controller.CustomerController;
import org.example.entity.Customer;
import org.example.util.ExportToExcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class CustomerManagementPanel extends JPanel {

    private final CustomerController controller = new CustomerController();

    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public CustomerManagementPanel() {
        initUI();
        initEvents();
        loadData();
    }

    /* ================= INIT UI ================= */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        /* ===== SEARCH ===== */
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtSearch = new JTextField(22);
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngưng"});

        JButton btnSearch = new JButton("🔍 Tìm");

        searchPanel.add(new JLabel("Tìm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(cbStatus);

        /* ===== ACTION ===== */
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        JButton btnExport = createButton("Xuất Excel", new Color(52, 152, 219));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnExport);

        /* ===== TABLE ===== */
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Mã KH", "Tên KH", "SĐT", "Email", "Điểm", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.removeColumn(table.getColumnModel().getColumn(0)); // ẩn ID

        JScrollPane scrollPane = new JScrollPane(table);

        /* ===== NORTH ===== */
        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(searchPanel, BorderLayout.CENTER);
        north.add(actionPanel, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnExport.addActionListener(e ->
                ExportToExcel.export(table, "DanhSachKhachHang.xlsx")
        );
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        List<Customer> list = controller.search(
                txtSearch.getText().trim(),
                Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
        );

        int stt = 1;
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    stt++,
                    c.getCode(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getPoint(),
                    c.getStatus() == 1 ? "Hoạt động" : "Ngưng"
            });
        }
    }

    /* ================= ADD ================= */
    private void openAddDialog() {
        JDialog d = createDialog("Thêm khách hàng");

        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JCheckBox chkActive = new JCheckBox("Hoạt động", true);

        JPanel form = createForm();
        addField(form, "Mã KH:", txtCode);
        addField(form, "Tên KH:", txtName);
        addField(form, "SĐT:", txtPhone);
        addField(form, "Email:", txtEmail);
        addField(form, "Trạng thái:", chkActive);

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                controller.add(
                        txtCode.getText(),
                        txtName.getText(),
                        txtPhone.getText(),
                        txtEmail.getText(),
                        chkActive.isSelected()
                );
                d.dispose();
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.add(form, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /* ================= EDIT ================= */
    private void openEditDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng cần sửa");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        Customer c = controller.findById(id);

        JDialog d = createDialog("Sửa khách hàng");

        JTextField txtCode = new JTextField(c.getCode());
        txtCode.setEditable(false);

        JTextField txtName = new JTextField(c.getName());
        JTextField txtPhone = new JTextField(c.getPhone());
        JTextField txtEmail = new JTextField(c.getEmail());
        JCheckBox chkActive = new JCheckBox("Hoạt động", c.getStatus() == 1);

        JPanel form = createForm();
        addField(form, "Mã KH:", txtCode);
        addField(form, "Tên KH:", txtName);
        addField(form, "SĐT:", txtPhone);
        addField(form, "Email:", txtEmail);
        addField(form, "Trạng thái:", chkActive);

        JButton btnSave = new JButton("Cập nhật");
        btnSave.addActionListener(e -> {
            try {
                controller.update(
                        id,
                        txtName.getText(),
                        txtPhone.getText(),
                        txtEmail.getText(),
                        chkActive.isSelected()
                );
                d.dispose();
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.add(form, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /* ================= DELETE ================= */
    private void deleteCustomer() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        if (JOptionPane.showConfirmDialog(
                this, "Xóa khách hàng này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            controller.delete(id);
            loadData();
        }
    }

    /* ================= UI HELPERS ================= */
    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }

    private JDialog createDialog(String title) {
        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        d.setSize(380, 320);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        return d;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        return p;
    }

    private void addField(JPanel p, String label, JComponent comp) {
        p.add(new JLabel(label));
        p.add(comp);
    }

    private void initEvents() {
        // để trống – đã gắn event trong initUI cho giống Account
    }
}
