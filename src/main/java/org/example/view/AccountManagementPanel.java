package org.example.view;

import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.entity.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private final AccountRepository account = new AccountRepositoryImpl();

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        // ===== TOP PANEL (SEARCH + FILTER) =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topPanel.setOpaque(false);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");

        cbStatus = new JComboBox<>(new String[]{
                "Tất cả", "Hoạt động", "Khóa"
        });

        topPanel.add(new JLabel("Tìm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);

        // ===== ACTION BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionPanel.setOpaque(false);

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // ===== TABLE =====
        String[] columns = {
                "STT",
                "Username",
                "Vai trò",
                "Trạng thái",
                "Ngày tạo",
                "Cập nhật"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // không cho sửa trực tiếp
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== LAYOUT =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ===== LOAD DATA =====
        loadDataFromDB();

        // ===== EVENTS =====
        btnRefresh.addActionListener(e -> loadDataFromDB());

        btnSearch.addActionListener(e -> loadDataFromDB());

        btnAdd.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Mở form THÊM tài khoản"));

        btnEdit.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Mở form SỬA tài khoản"));

        btnDelete.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "XÓA tài khoản"));
    }

    // ===== LOAD DATA FROM DATABASE =====
    private void loadDataFromDB() {
        tableModel.setRowCount(0);

        String keyword = txtSearch.getText().trim().toLowerCase();
        String statusFilter = cbStatus.getSelectedItem().toString();

        List<Account> accounts = account.findAll();

        int stt = 1;
        for (Account acc : accounts) {

            // filter username
            if (!keyword.isEmpty()
                    && !acc.getUsername().toLowerCase().contains(keyword)) {
                continue;
            }

            // filter status
            String statusText = acc.isActive() ? "Hoạt động" : "Khóa";
            if (!statusFilter.equals("Tất cả")
                    && !statusFilter.equals(statusText)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    stt++,
                    acc.getUsername(),
                    acc.getRole(),
                    statusText,
                    acc.getCreatedTime(),
                    acc.getUpdateTime()
            });
        }
    }

    // ===== BUTTON STYLE =====
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }
}
