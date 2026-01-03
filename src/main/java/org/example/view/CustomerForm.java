package org.example.view;

import org.example.controller.CustomerController;
import org.example.entity.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerForm extends JDialog {

    private JTextField txtName, txtPhone, txtEmail;
    private final Customer customer;
    private final Runnable callback;
    private final CustomerController controller = new CustomerController();

    public CustomerForm(Customer customer, Runnable callback) {
        this.customer = customer;
        this.callback = callback;

        setTitle(customer == null ? "Add Customer" : "Edit Customer");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setModal(true);
        initUI();
        if (customer != null) fillData();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        add(new JLabel("Name")); add(txtName);
        add(new JLabel("Phone")); add(txtPhone);
        add(new JLabel("Email")); add(txtEmail);
        add(btnSave); add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
    }

    private void save() {
        if (customer == null) {
            controller.create(txtName.getText(), txtPhone.getText(), txtEmail.getText());
        } else {
            customer.setName(txtName.getText());
            customer.setPhone(txtPhone.getText());
            customer.setEmail(txtEmail.getText());
            controller.update(customer);
        }
        callback.run();
        dispose();
    }
}
