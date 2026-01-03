package org.example.view;

import org.example.controller.OrderController;
import org.example.entity.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    private final OrderController controller = new OrderController();
    private List<Order> orderList;

    public OrderManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        /* ===== TOP ===== */
        JLabel title = new JLabel("QUẢN LÝ ĐƠN HÀNG");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");

        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDelete = new JButton("🗑 Xóa");
        JButton btnRefresh = new JButton("🔄 Refresh");
        JButton btnDetail = new JButton("📋 Xem / Thêm món");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(title);
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        top.add(btnRefresh);
        top.add(btnDetail);

        add(top, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new Object[]{"STT", "Khách hàng", "Tổng tiền", "Ngày tạo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        btnAdd.addActionListener(e ->
                new OrderForm(this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editOrder());
        btnDelete.addActionListener(e -> deleteOrder());
        btnDetail.addActionListener(e -> openDetail());
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        model.setRowCount(0);

        String keyword = txtSearch.getText().toLowerCase();
        orderList = controller.getAll();

        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int stt = 1;
        for (Order o : orderList) {

            boolean match =
                    keyword.isBlank()
                            || (o.getCustomerName() != null
                            && o.getCustomerName().toLowerCase().contains(keyword));

            if (match) {
                model.addRow(new Object[]{
                        stt++,
                        o.getCustomerName() == null ? "Khách lẻ" : o.getCustomerName(),
                        o.getTotalAmount(),
                        o.getCreatedTime().format(fmt)
                });
            }
        }
    }

    /* ================= SỬA ĐƠN ================= */
    private void editOrder() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đơn cần sửa");
            return;
        }

        Order order = orderList.get(row);

        JTextField txtCustomer =
                new JTextField(order.getCustomerName());

        Object[] form = {
                "Tên khách hàng:", txtCustomer
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Sửa đơn hàng",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            // ⚠️ Demo đơn giản – thực tế nên chọn customerId
            JOptionPane.showMessageDialog(
                    this,
                    "Phần sửa khách nên làm bằng ComboBox khách hàng.\n" +
                            "Hiện tại chỉ demo UI.",
                    "Lưu ý",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /* ================= XÓA ĐƠN ================= */
    private void deleteOrder() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đơn cần xóa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa đơn hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = orderList.get(row).getId();
            controller.delete(id);
            loadData();
        }
    }

    /* ================= XEM CHI TIẾT ================= */
    private void openDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đơn hàng");
            return;
        }

        Order order = orderList.get(row);

        MainFrame frame =
                (MainFrame) SwingUtilities.getWindowAncestor(this);

        frame.showOrderDetail(order);
    }
}
