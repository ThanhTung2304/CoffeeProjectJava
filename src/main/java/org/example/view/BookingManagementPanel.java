package org.example.view;

import org.example.controller.ReservationController;
import org.example.entity.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingManagementPanel extends JPanel {

    private final ReservationController controller = new ReservationController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public BookingManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Tiêu đề =====
        JLabel title = new JLabel("QUẢN LÝ ĐẶT BÀN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Thanh tìm kiếm và lọc =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        searchPanel.setBackground(new Color(245, 245, 245));

        searchField = new JTextField(15);
        JButton btnSearch = new JButton("🔍 Tìm");
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang đặt", "Hoàn thành", "Hủy"});

        searchPanel.add(new JLabel("Tìm:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(Box.createHorizontalStrut(30));
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusFilter);

        add(searchPanel, BorderLayout.PAGE_START);

        // ===== Thanh nút chức năng =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton btnAdd = createButton("Thêm", new Color(0, 153, 76));
        JButton btnEdit = createButton("Sửa", new Color(255, 153, 0));
        JButton btnDelete = createButton("Xóa", new Color(204, 0, 0));
        JButton btnRefresh = createButton("Refresh", new Color(0, 102, 204));

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteBooking());
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // ===== Bảng dữ liệu =====
        String[] columns = {"ID", "Tên khách hàng", "Bàn số", "Thời gian", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 250));
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ===== Xử lý tìm kiếm =====
        btnSearch.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            String status = (String) statusFilter.getSelectedItem();

            List<Reservation> filtered = controller.getAllReservations();

            if (!keyword.isEmpty()) {
                filtered = filtered.stream()
                        .filter(r -> r.getCustomerName().toLowerCase().contains(keyword.toLowerCase()))
                        .toList();
            }

            if (!status.equals("Tất cả")) {
                filtered = filtered.stream()
                        .filter(r -> r.getStatus().equalsIgnoreCase(status))
                        .toList();
            }

            tableModel.setRowCount(0);
            for (Reservation r : filtered) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getCustomerName(),
                        r.getTableNumber(),
                        r.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        r.getStatus(),
                        r.getNote()
                });
            }
        });

        loadData();
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = controller.getAllReservations();
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getCustomerName(),
                    r.getTableNumber(),
                    r.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    r.getStatus(),
                    r.getNote()
            });
        }
    }

    private void showAddDialog() {
        JTextField customerNameField = new JTextField();
        JTextField tableNumberField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Tên khách hàng:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Số bàn:"));
        panel.add(tableNumberField);
        panel.add(new JLabel("Thời gian (yyyy-MM-dd HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusField);
        panel.add(new JLabel("Ghi chú:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Thêm đặt bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Reservation r = new Reservation();
                r.setCustomerName(customerNameField.getText());
                r.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                r.setTime(LocalDateTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                r.setStatus(statusField.getText());
                r.setNote(noteField.getText());
                controller.addReservation(r);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Reservation r = controller.getAllReservations().get(row);

            JTextField customerNameField = new JTextField(r.getCustomerName());
            JTextField tableNumberField = new JTextField(String.valueOf(r.getTableNumber()));
            JTextField timeField = new JTextField(r.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            JTextField statusField = new JTextField(r.getStatus());
            JTextField noteField = new JTextField(r.getNote());

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Tên khách hàng:"));
            panel.add(customerNameField);
            panel.add(new JLabel("Số bàn:"));
            panel.add(tableNumberField);
            panel.add(new JLabel("Thời gian (yyyy-MM-dd HH:mm):"));
            panel.add(timeField);
            panel.add(new JLabel("Trạng thái:"));
            panel.add(statusField);
            panel.add(new JLabel("Ghi chú:"));
            panel.add(noteField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Sửa đặt bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    r.setCustomerName(customerNameField.getText());
                    r.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                    r.setTime(LocalDateTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    r.setStatus(statusField.getText());
                    r.setNote(noteField.getText());
                    controller.updateReservation(r);
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
        }
    }

    private void deleteBooking() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Reservation r = controller.getAllReservations().get(row);
            controller.deleteReservation(r.getId());
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
        }
    }
}
