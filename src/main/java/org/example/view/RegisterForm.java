package org.example.view;

//public class RegisterForm {
//    public void setVisible(boolean b) {
//
//    }
//}


import org.example.dto.RegisterRequest;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JButton btnRegister;
    private JLabel linkLogin;

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

        /* ===== IMAGE + TITLE ===== */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/logoHighland.png")

        );
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
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
        JPanel form = new JPanel(new GridLayout(6, 1, 0, 12));
        form.setBackground(Color.WHITE);

        txtUsername = createTextField("Username");
        txtPassword = createPasswordField("Password");
        txtConfirm  = createPasswordField("Confirm password");

        btnRegister = new JButton("ĐĂNG KÝ");
        btnRegister.setBackground(new Color(70, 130, 180));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(200, 40));

        btnRegister.addActionListener(e -> onRegister());

        linkLogin = new JLabel("Đã có tài khoản? Đăng nhập");
        linkLogin.setForeground(new Color(70, 130, 180));
        linkLogin.setHorizontalAlignment(SwingConstants.CENTER);
        linkLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        form.add(txtUsername);
        form.add(txtPassword);
        form.add(txtConfirm);
        form.add(Box.createVerticalStrut(5));
        form.add(btnRegister);
        form.add(linkLogin);

        main.add(form, BorderLayout.CENTER);
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        return field;
    }

    private void onRegister() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername(txtUsername.getText().trim());
        req.setPassword(new String(txtPassword.getPassword()));
        req.setConfirmPassword(new String(txtConfirm.getPassword()));

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
