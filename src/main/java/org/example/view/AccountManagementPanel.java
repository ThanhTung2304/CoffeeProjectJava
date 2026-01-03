package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.util.ExportToExcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AccountManagementPanel extends JPanel {

    private final AccountController controller = new AccountController();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtSearch = new JTextField(22);
    private final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Khóa"});

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        /* ===== TITLE ===== */
        JLabel title = new JLabel("QUẢN LÝ TÀI KHOẢN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        /* ===== SEARCH ===== */
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
                new String[]{"ID", "STT", "Username", "Password", "Role", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID

        JScrollPane scroll = new JScrollPane(table);

        /* ===== NORTH ===== */
        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(searchPanel, BorderLayout.CENTER);
        north.add(actionPanel, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteAccount());
        btnExport.addActionListener(e -> ExportToExcel.export(table, "DanhSachTaiKhoan.xlsx"));

        loadData();
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        List<Account> list = controller.search(
                txtSearch.getText(),
                Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
        );

        int stt = 1;
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    stt++,
                    a.getUsername(),
                    a.getPassword(),
                    a.getRole(),
                    a.isActive() ? "Hoạt động" : "Khóa"
            });
        }
    }

    /* ================= ADD ================= */
    private void openAddDialog() {
        JDialog d = createDialog("Thêm tài khoản");

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        JCheckBox chkActive = new JCheckBox("Hoạt động", true);

        JPanel form = createForm();
        addField(form, "Username:", txtUser);
        addField(form, "Password:", txtPass);
        addField(form, "Role:", cbRole);
        addField(form, "Trạng thái:", chkActive);

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> {
            try {
                controller.add(
                        txtUser.getText(),
                        new String(txtPass.getPassword()),
                        Objects.requireNonNull(Objects.requireNonNull(cbRole.getSelectedItem())).toString(),
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
            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần sửa");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String username = tableModel.getValueAt(modelRow, 2).toString();

        Account acc = controller.findByUsername(username);

        JDialog d = createDialog("Sửa tài khoản");

        JTextField txtUser = new JTextField(acc.getUsername());
        txtUser.setEditable(true);

        JPasswordField txtPass = new JPasswordField(acc.getPassword());
        JPasswordField txtConfirm = new JPasswordField(acc.getPassword());

        JComboBox<String> cbRole =
                new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        cbRole.setSelectedItem(acc.getRole());

        JCheckBox chkActive = new JCheckBox("Hoạt động", acc.isActive());

        JPanel form = createForm();
        addField(form, "Username:", txtUser);
        addField(form, "Password:", txtPass);
        addField(form, "Confirm:", txtConfirm);
        addField(form, "Role:", cbRole);
        addField(form, "Trạng thái:", chkActive);

        JButton btnSave = new JButton("Cập nhật");
        btnSave.addActionListener(e -> {
            String pass = new String(txtPass.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(d, "Mật khẩu không khớp!");
                return;
            }

            try {
                controller.update(
                        id,
                        acc.getUsername(),
                        pass,              // cập nhật mật khẩu
                        Objects.requireNonNull(Objects.requireNonNull(cbRole.getSelectedItem())).toString(),
                        chkActive.isSelected()
                );
                d.dispose();
                loadData();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.add(form, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }


    /* ================= DELETE ================= */
    private void deleteAccount() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        if (JOptionPane.showConfirmDialog(
                this, "Xóa tài khoản này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            controller.delete(id);
            loadData();
        }
    }

    /* ================= UI HELPERS ================= */
    private JButton createButton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
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
        d.setSize(360, 300);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        return d;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        return p;
    }

    private void addField(JPanel p, String l, JComponent c) {
        p.add(new JLabel(l));
        p.add(c);
    }
}
