package org.example.view;

import org.example.dto.RegisterRequest;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class RegisterForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JComboBox<String> cbRole;

    private final AuthService authService = new AuthServiceImpl();

    public RegisterForm() {
        setTitle("Đăng ký");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(main);

        /* ===== TOP ===== */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/logoHighland.png"))
        );
        Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel(new ImageIcon(img));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        topPanel.add(lblImage);
        topPanel.add(title);
        main.add(topPanel, BorderLayout.NORTH);

        /* ===== FORM ===== */
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirm  = new JPasswordField();

        cbRole = new JComboBox<>(new String[]{"USER", "STAFF"});
        cbRole.setMaximumSize(new Dimension(200, 30));
        cbRole.setPreferredSize(new Dimension(200, 30));

        form.add(createRow("Username", txtUsername));
        form.add(Box.createVerticalStrut(12));

        form.add(createRow("Password", txtPassword));
        form.add(Box.createVerticalStrut(12));

        form.add(createRow("ConfirmPassword", txtConfirm));
        form.add(Box.createVerticalStrut(12));

        form.add(createRow("Role", cbRole));
        form.add(Box.createVerticalStrut(25));

        JButton btnRegister = createRegisterButton();
        form.add(btnRegister);
        form.add(Box.createVerticalStrut(18));

        JLabel linkLogin = new JLabel("Đã có tài khoản? Đăng nhập");
        linkLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        linkLogin.setForeground(new Color(70, 130, 180));
        linkLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        form.add(linkLogin);
        main.add(form, BorderLayout.CENTER);
    }

    /* ===== BUTTON ===== */
    private JButton createRegisterButton() {
        JButton btn = new JButton("ĐĂNG KÝ");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> onRegister());
        return btn;
    }

    /* ===== ROW ===== */
    private JPanel createRow(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(360, 35));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(140, 30));
        label.setMaximumSize(new Dimension(140, 30));

        field.setPreferredSize(new Dimension(200, 30));
        field.setMaximumSize(new Dimension(200, 30));

        row.add(label);
        row.add(Box.createHorizontalStrut(10));
        row.add(field);

        return row;
    }

    /* ===== REGISTER ACTION ===== */
    private void onRegister() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername(txtUsername.getText().trim());
        req.setPassword(new String(txtPassword.getPassword()));
        req.setConfirmPassword(new String(txtConfirm.getPassword()));
        req.setRole(Objects.requireNonNull(cbRole.getSelectedItem()).toString());

        try {
            authService.register(req);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            dispose();
            new LoginForm().setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
