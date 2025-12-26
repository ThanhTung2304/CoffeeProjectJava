package org.example.view;

import org.example.entity.Account;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AccountManagementPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final AccountService accountService = new AccountServiceImpl();

    private final JTextField txtSearch;
    private final JComboBox<String> cbStatus;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        /* ===== TITLE ===== */
        JLabel title = new JLabel("QUẢN LÝ TÀI KHOẢN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        /* ===== SEARCH ===== */
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Khóa"});

        topPanel.add(new JLabel("Tìm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);

        /* ===== ACTION ===== */
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        /* ===== TABLE ===== */
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Username", "Role", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);

        // Ẩn cột ID
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(table);

        /* ===== LAYOUT ===== */
        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(topPanel, BorderLayout.CENTER);
        north.add(actionPanel, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnRefresh.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteAccount());

        loadData();
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        String keyword = txtSearch.getText().trim().toLowerCase();
        String status = cbStatus.getSelectedItem().toString();

        List<Account> list = accountService.findAll();
        int stt = 1;

        for (Account a : list) {

            if (!keyword.isEmpty()
                    && !a.getUsername().toLowerCase().contains(keyword)) {
                continue;
            }

            String st = a.isActive() ? "Hoạt động" : "Khóa";
            if (!status.equals("Tất cả") && !status.equals(st)) continue;

            tableModel.addRow(new Object[]{
                    a.getId(),
                    stt++,
                    a.getUsername(),
                    a.getRole(),
                    st
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
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Không được để trống");
                return;
            }

            if (accountService.existsByUsername(username)) {
                JOptionPane.showMessageDialog(d, "Username đã tồn tại");
                return;
            }

            accountService.create(new Account(
                    username,
                    password,
                    Objects.requireNonNull(cbRole.getSelectedItem()).toString(),
                    chkActive.isSelected()
            ));

            d.dispose();
            loadData();
        });

        d.add(form, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /* ================= EDIT ================= */
    private void openEditDialog() {

        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn tài khoản cần sửa",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // CHUYỂN VIEW → MODEL (BẮT BUỘC)
        int modelRow = table.convertRowIndexToModel(viewRow);

        // Username nằm ở cột index = 2 trong MODEL
        String username = tableModel.getValueAt(modelRow, 2).toString().trim();

        System.out.println("DEBUG username = [" + username + "]");

        Account acc = accountService.findByUsername(username);

        if (acc == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy tài khoản trong DB!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog d = createDialog("Sửa tài khoản");

        JTextField txtUser = new JTextField(acc.getUsername());
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        cbRole.setSelectedItem(acc.getRole());
        JCheckBox chkActive = new JCheckBox("Hoạt động", acc.isActive());

        JPanel form = createForm();
        addField(form, "Username:", txtUser);
        addField(form, "Role:", cbRole);
        addField(form, "Trạng thái:", chkActive);

        JButton btnSave = new JButton("Cập nhật");
        btnSave.addActionListener(e -> {
            acc.setUsername(txtUser.getText().trim());
            acc.setRole(cbRole.getSelectedItem().toString());
            acc.setActive(chkActive.isSelected());

            accountService.update(acc);
            d.dispose();
            loadData();
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

        int c = JOptionPane.showConfirmDialog(
                this,
                "Xóa tài khoản này ?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (c == JOptionPane.YES_OPTION) {
            accountService.deleteById(id);
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
