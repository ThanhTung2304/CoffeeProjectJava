package org.example.view;

import org.example.controller.EmployeeController;
import org.example.entity.Employee;

import javax.swing.*;
import java.awt.*;

public class EmployeeForm extends JDialog {

    private JTextField txtName, txtPhone, txtAccountId;
    private JComboBox<String> cbPosition;

    private final EmployeeController controller = new EmployeeController();
    private final Employee employee;
    private final Runnable callback;

    public EmployeeForm(Employee employee, Runnable callback) {
        this.employee = employee;
        this.callback = callback;

        setTitle(employee == null ? "Add Employee" : "Edit Employee");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
        if (employee != null) fillData();
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2, 10, 10));

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtAccountId = new JTextField();

        cbPosition = new JComboBox<>(new String[]{"Admin", "Staff"});

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        add(new JLabel("Name"));
        add(txtName);
        add(new JLabel("Phone"));
        add(txtPhone);
        add(new JLabel("Position"));
        add(cbPosition);
        add(new JLabel("Account ID"));
        add(txtAccountId);
        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(employee.getName());
        txtPhone.setText(employee.getPhone());
        cbPosition.setSelectedItem(employee.getPosition());
        txtAccountId.setText(
                employee.getAccountId() == null ? "" : employee.getAccountId().toString()
        );
    }

    private void save() {
        String name = txtName.getText();
        String phone = txtPhone.getText();
        String position = cbPosition.getSelectedItem().toString();
        Integer accountId = txtAccountId.getText().isBlank()
                ? null : Integer.parseInt(txtAccountId.getText());

        if (employee == null) {
            controller.create(name, phone, position, accountId);
        } else {
            employee.setName(name);
            employee.setPhone(phone);
            employee.setPosition(position);
            employee.setAccountId(accountId);
            controller.update(employee);
        }

        callback.run();
        dispose();
    }
}
