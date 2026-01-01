package org.example.view;

import org.example.controller.TableController;
import org.example.entity.TableSeat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TableManagementPanel extends JPanel {
    private final TableController controller = new TableController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public TableManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Tiêu đề
        JLabel title = new JLabel("QUẢN LÝ BÀN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Panel tìm kiếm + trạng thái + nút Tìm (một hàng, căn trái)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        searchPanel.add(btnSearch);

        searchPanel.add(new JLabel("Trạng thái:"));
        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang sử dụng", "Đặt trước"});
        searchPanel.add(statusFilter);

        // Gắn sự kiện cho combobox để lọc ngay khi chọn
        statusFilter.addActionListener(e -> filterData());

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(0, 153, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showAddDialog());

        JButton btnEdit = new JButton("Sửa");
        btnEdit.setBackground(new Color(255, 153, 0));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> showEditDialog());

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(204, 0, 0));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteTable());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Bảng dữ liệu
        String[] columns = {"Số bàn", "Tên bàn", "Sức chứa", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);

        // Gộp topPanel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<TableSeat> tables = controller.getAllTables();
        for (TableSeat t : tables) {
            tableModel.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }
    }

    private void filterData() {
        String keyword = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();

        List<TableSeat> tables = controller.getAllTables().stream()
                .filter(t -> (keyword.isEmpty() ||
                        String.valueOf(t.getTableNumber()).contains(keyword) ||
                        (t.getNote() != null && t.getNote().toLowerCase().contains(keyword)))
                        && ("Tất cả".equals(status) || t.getStatus().equals(status)))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (TableSeat t : tables) {
            tableModel.addRow(new Object[]{
                    t.getTableNumber(),
                    t.getName(),
                    t.getCapacity(),
                    t.getStatus(),
                    t.getNote()
            });
        }
    }

    private void showAddDialog() {
        JTextField numberField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Số bàn:")); panel.add(numberField);
        panel.add(new JLabel("Tên bàn:")); panel.add(nameField);
        panel.add(new JLabel("Sức chứa:")); panel.add(capacityField);
        panel.add(new JLabel("Trạng thái:")); panel.add(statusField);
        panel.add(new JLabel("Ghi chú:")); panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Thêm bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                TableSeat t = new TableSeat();
                t.setTableNumber(Integer.parseInt(numberField.getText()));
                t.setName(nameField.getText());
                t.setCapacity(Integer.parseInt(capacityField.getText()));
                t.setStatus(statusField.getText());
                t.setNote(noteField.getText());
                controller.addTable(t);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int tableNumber = (int) tableModel.getValueAt(row, 0);
            TableSeat t = controller.getAllTables().stream()
                    .filter(tb -> tb.getTableNumber() == tableNumber)
                    .findFirst().orElse(null);
            if (t == null) return;

            JTextField nameField = new JTextField(t.getName());
            JTextField capacityField = new JTextField(String.valueOf(t.getCapacity()));
            JTextField statusField = new JTextField(t.getStatus());
            JTextField noteField = new JTextField(t.getNote());

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Tên bàn:")); panel.add(nameField);
            panel.add(new JLabel("Sức chứa:")); panel.add(capacityField);
            panel.add(new JLabel("Trạng thái:")); panel.add(statusField);
            panel.add(new JLabel("Ghi chú:")); panel.add(noteField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Sửa bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    t.setName(nameField.getText());
                    t.setCapacity(Integer.parseInt(capacityField.getText()));
                    t.setStatus(statusField.getText());
                    t.setNote(noteField.getText());
                    controller.updateTable(t);
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
        }
    }

    private void deleteTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int tableNumber = (int) tableModel.getValueAt(row, 0);
            controller.deleteTable(tableNumber);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xoá!");
        }
    }
}