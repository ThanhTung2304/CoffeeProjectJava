package org.example.view;

import org.example.controller.OrderItemController;
import org.example.entity.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderItemPanel extends JPanel {

    private final int orderId;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;

    private final OrderItemController controller =
            new OrderItemController();

    public OrderItemPanel(int orderId) {
        this.orderId = orderId;
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadData();
    }

    private void initUI() {

        model = new DefaultTableModel(
                new Object[]{"Tên món", "Giá", "Số lượng", "Thành tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        JButton btnAdd = new JButton("➕ Thêm món");
        JButton btnEdit = new JButton("✏️ Sửa món");

        JButton btnDelete = new JButton("🗑 Xóa món");

        lblTotal = new JLabel("Tổng: 0 đ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(lblTotal);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addItem());
        btnEdit.addActionListener(e -> editItem());
        btnDelete.addActionListener(e -> deleteItem());
    }

    private void loadData() {
        model.setRowCount(0);

        List<OrderItem> list = controller.getByOrder(orderId);
        int total = 0;

        for (OrderItem item : list) {
            model.addRow(new Object[]{
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTotal()
            });
            total += item.getTotal();
        }

        lblTotal.setText("Tổng: " + total + " đ");
    }

    private void addItem() {
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtQty = new JTextField();

        Object[] form = {
                "Tên món:", txtName,
                "Giá:", txtPrice,
                "Số lượng:", txtQty
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Thêm món",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.addItem(
                        orderId,
                        txtName.getText(),
                        Integer.parseInt(txtQty.getText()),
                        Integer.parseInt(txtPrice.getText())
                );
                loadData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Giá và số lượng phải là số",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    private void editItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn món cần sửa");
            return;
        }

        OrderItem item =
                controller.getByOrder(orderId).get(row);

        JTextField txtPrice =
                new JTextField(String.valueOf(item.getPrice()));
        JTextField txtQty =
                new JTextField(String.valueOf(item.getQuantity()));

        Object[] form = {
                "Giá:", txtPrice,
                "Số lượng:", txtQty
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Sửa món",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.updateItem(
                        item.getId(),
                        Integer.parseInt(txtPrice.getText()),
                        Integer.parseInt(txtQty.getText())
                );
                loadData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Giá & số lượng phải là số",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn món cần xóa");
            return;
        }

        List<OrderItem> list = controller.getByOrder(orderId);
        int itemId = list.get(row).getId();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa món này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteItem(itemId);
            loadData();
        }
    }
}
