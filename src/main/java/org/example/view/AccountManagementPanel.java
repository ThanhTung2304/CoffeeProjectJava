package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.event.DataChangeEventBus;
import org.example.util.ExportToExcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AccountManagementPanel extends JPanel {

    private final AccountController controller = new AccountController();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        initUI();
        loadData();

        // FIX: lắng nghe event khi có data thay đổi (đăng ký, thêm, sửa, xóa)
        DataChangeEventBus.onRegister(this::loadData);
    }

    private void initUI() {

        /* ===== HEADER ===== */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Quản lý tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 30));

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Khóa"});

        JButton btnSearch = createButton("🔍", new Color(52, 152, 219));

        right.add(txtSearch);
        right.add(cbStatus);
        right.add(btnSearch);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        /* ===== ACTION ===== */
        JPanel action = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        action.setBackground(new Color(240, 242, 245));

        JButton btnAdd     = createButton("Thêm",    new Color(46, 204, 113));
        JButton btnEdit    = createButton("Sửa",     new Color(241, 196, 15));
        JButton btnDelete  = createButton("Xóa",     new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        JButton btnExport  = createButton("Excel",   new Color(52, 152, 219));

        action.add(btnAdd);
        action.add(btnEdit);
        action.add(btnDelete);
        action.add(btnRefresh);
        action.add(btnExport);

        /* ===== TABLE ===== */
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Username", "Password", "Role", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (s) {
                    comp.setBackground(new Color(189, 215, 238));
                } else {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return comp;
            }
        });

        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.add(scroll);

        /* ===== MAIN ===== */
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(240, 242, 245));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        main.add(action, BorderLayout.NORTH);
        main.add(card,   BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(main,   BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteAccount());
        btnExport.addActionListener(e -> ExportToExcel.export(table, "DanhSachTaiKhoan.xlsx"));
    }

    /* ===== LOAD DATA ===== */
    private void loadData() {
        // FIX: đảm bảo chạy trên EDT khi được gọi từ event bus
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::loadData);
            return;
        }

        tableModel.setRowCount(0);

        List<Account> list = controller.search(
                txtSearch.getText(),
                Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
        );

        int stt = 1;
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(), stt++, a.getUsername(),
                    a.getPassword(), a.getRole(),
                    a.isActive() ? "Hoạt động" : "Khóa"
            });
        }
    }

    private void openAddDialog() {
        JTextField user    = new JTextField();
        JPasswordField pass = new JPasswordField();

        JComboBox<String> role   = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        JCheckBox         active = new JCheckBox("Hoạt động", true);

        JPanel p = createForm();
        addField(p, "Username:",   user);
        addField(p, "Password:",   pass);
        addField(p, "Role:",       role);
        addField(p, "Trạng thái:", active);

        if (JOptionPane.showConfirmDialog(this, p, "Thêm", JOptionPane.OK_CANCEL_OPTION) == 0) {
            controller.add(
                    user.getText(),
                    new String(pass.getPassword()),
                    Objects.requireNonNull(role.getSelectedItem()).toString(),
                    active.isSelected()
            );
            loadData();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id       = (int) tableModel.getValueAt(modelRow, 0);

        String  username = tableModel.getValueAt(modelRow, 2).toString();
        Account acc      = controller.findByUsername(username);

        JTextField    user   = new JTextField(acc.getUsername());
        JPasswordField pass  = new JPasswordField(acc.getPassword());

        JComboBox<String> role   = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        role.setSelectedItem(acc.getRole());

        JCheckBox active = new JCheckBox("Hoạt động", acc.isActive());

        JPanel p = createForm();
        addField(p, "Username:",   user);
        addField(p, "Password:",   pass);
        addField(p, "Role:",       role);
        addField(p, "Trạng thái:", active);

        if (JOptionPane.showConfirmDialog(this, p, "Sửa", JOptionPane.OK_CANCEL_OPTION) == 0) {
            controller.update(
                    id,
                    user.getText(),
                    new String(pass.getPassword()),
                    Objects.requireNonNull(role.getSelectedItem()).toString(),
                    active.isSelected()
            );
            loadData();
        }
    }

    private void deleteAccount() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        int id       = (int) tableModel.getValueAt(modelRow, 0);

        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản?") == 0) {
            controller.delete(id);
            loadData();
        }
    }

    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        return p;
    }

    private void addField(JPanel p, String l, JComponent c) {
        p.add(new JLabel(l));
        p.add(c);
    }
}