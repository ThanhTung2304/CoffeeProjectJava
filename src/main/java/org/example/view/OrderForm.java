package org.example.view;

import org.example.controller.CustomerController;
import org.example.controller.OrderController;
import org.example.entity.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderForm extends JDialog {

    private final CustomerController customerController =
            new CustomerController();

    private JComboBox<Customer> cbCustomer;
    private final OrderController controller = new OrderController();
    private final Runnable onSuccess;

    public OrderForm(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        setTitle("Tạo đơn hàng");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setModal(true);
        initUI();
    }

    private void initUI() {

        cbCustomer = new JComboBox<>();
        cbCustomer.addItem(null); // Khách lẻ

        // ⭐ LOAD DANH SÁCH KHÁCH
        List<Customer> customers =
                customerController.getAll();

        for (Customer c : customers) {
            cbCustomer.addItem(c);
        }

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel form = new JPanel(new GridLayout(1, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("Khách hàng"));
        form.add(cbCustomer);

        JPanel bottom = new JPanel();
        bottom.add(btnSave);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }


    private void save() {
        Customer c = (Customer) cbCustomer.getSelectedItem();
        Integer customerId = (c == null) ? null : c.getId();

        controller.create(customerId, 0); // 🔥 total = 0
        onSuccess.run();
        dispose();
    }
}

