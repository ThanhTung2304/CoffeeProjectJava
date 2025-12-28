package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.EmployeeController;
import org.example.entity.Account;
import org.example.entity.Employee;

import javax.swing.*;
import java.awt.*;

public class EmployeeForm extends JDialog {

    private JTextField txtName, txtPhone;
    private JComboBox<String> cbPosition;
    private JComboBox<Account> cbAccount;

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

        cbPosition = new JComboBox<>(new String[]{"Staff"});

        cbAccount = new JComboBox<>();
        loadAccount();

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        add(new JLabel("Name"));
        add(txtName);

        add(new JLabel("Phone"));
        add(txtPhone);

        add(new JLabel("Position"));
        add(cbPosition);

        add(new JLabel("Account"));
        add(cbAccount);

        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(employee.getName());
        txtPhone.setText(employee.getPhone());
        cbPosition.setSelectedItem(employee.getPosition());

        if (employee.getAccountId() != null) {
            for (int i = 0; i < cbAccount.getItemCount(); i++) {
                Account acc = cbAccount.getItemAt(i);
                if (acc != null && employee.getAccountId().equals(acc.getId())) {
                    cbAccount.setSelectedIndex(i);
                    break;
                }
            }
        }
    }


    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String position = cbPosition.getSelectedItem().toString();

        Account acc = (Account) cbAccount.getSelectedItem();
        Integer accountId = (acc == null) ? null : acc.getId();

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

    private void loadAccount() {
        AccountController accController = new AccountController();

        cbAccount.addItem(null); // Không gán account

        accController.loadAccounts().forEach(cbAccount::addItem);

        cbAccount.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("-- Không gán --");
                } else {
                    Account acc = (Account) value;
                    setText(acc.getUsername());
                }
                return this;
            }
        });
    }
}
