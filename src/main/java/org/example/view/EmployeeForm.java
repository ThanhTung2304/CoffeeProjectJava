package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.EmployeeController;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.event.DataChangeEventBus;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EmployeeForm extends JDialog {

    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbPosition;
    private JComboBox<String> cbRole;

    private final EmployeeController employeeController = new EmployeeController();
    private final AccountController accountController = new AccountController();
    private final AccountRepository accountRepository = new AccountRepositoryImpl();

    private final Employee employee;
    private final Runnable callback;

    public EmployeeForm(Employee employee, Runnable callback) {
        this.employee = employee;
        this.callback = callback;

        setTitle(employee == null ? "Thêm Nhân Viên" : "Sửa Nhân Viên");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
        if (employee != null) fillData();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtName = new JTextField();
        txtPhone = new JTextField();
        cbPosition = new JComboBox<>(new String[]{"Staff", "Admin"});

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{"STAFF", "ADMIN"});

        panel.add(createLabel("Tên nhân viên:"));
        panel.add(txtName);

        panel.add(createLabel("SĐT:"));
        panel.add(txtPhone);

        panel.add(createLabel("Chức vụ:"));
        panel.add(cbPosition);

        panel.add(createSeparator("── Tài khoản liên kết ──"));
        panel.add(new JLabel(""));

        panel.add(createLabel("Username:"));
        panel.add(txtUsername);

        panel.add(createLabel("Password:"));
        panel.add(txtPassword);

        panel.add(createLabel("Phân quyền:"));
        panel.add(cbRole);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnSave.setBackground(new Color(0x22C55E));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(0xEF4444));
        btnCancel.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(employee.getName());
        txtPhone.setText(employee.getPhone());
        cbPosition.setSelectedItem(employee.getPosition());

        if (employee.getAccountId() != null) {
            Account acc = accountRepository.findById(employee.getAccountId().longValue()).orElse(null);
            if (acc != null) {
                txtUsername.setText(acc.getUsername());
                cbRole.setSelectedItem(acc.getRole());
                txtUsername.setEditable(false);
            }
        }
    }

    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String position = Objects.requireNonNull(cbPosition.getSelectedItem()).toString();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = Objects.requireNonNull(cbRole.getSelectedItem()).toString();

        if (employee == null) {
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (accountController.findByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            accountController.add(username, password, role, true);
            Account savedAcc = accountController.findByUsername(username);

            Integer accId = savedAcc != null ? savedAcc.getId() : null;
            String accUsername = savedAcc != null ? savedAcc.getUsername() : null;

            employeeController.create(name, phone.isEmpty() ? "" : phone, position, accId);

        } else {
            employee.setName(name);
            employee.setPhone(phone);
            employee.setPosition(position);

            if (employee.getAccountId() != null) {
                Account existingAcc = accountRepository.findById(employee.getAccountId().longValue()).orElse(null);
                if (existingAcc != null) {
                    existingAcc.setRole(role);
                    if (!password.isEmpty()) {
                        existingAcc.setPassword(password);
                    }
                    accountController.update(existingAcc.getId(), existingAcc.getUsername(), existingAcc.getPassword(), existingAcc.getRole(), existingAcc.isActive());
                }
            } else {
                if (!username.isEmpty() && !password.isEmpty()) {
                    if (accountController.findByUsername(username) != null) {
                        JOptionPane.showMessageDialog(this, "Username đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    accountController.add(username, password, role, true);
                    Account savedAcc = accountController.findByUsername(username);
                    if (savedAcc != null) {
                        employee.setAccountId(savedAcc.getId());
                        employee.setUsername(savedAcc.getUsername());
                    }
                }
            }

            employeeController.update(employee);
        }

        DataChangeEventBus.notifyChange();
        callback.run();
        dispose();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JLabel createSeparator(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x64748B));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }
}
