package org.example.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private String username;

    // Constructor nhận username
    public MainFrame(String username) {
        this.username = username;

        setTitle("Trang chính - Xin chào " + username);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ===== HEADER =====
        JLabel lblWelcome = new JLabel("Xin chào " + username, SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        mainPanel.add(lblWelcome, BorderLayout.NORTH);

        // ===== CONTENT =====
        JTextArea content = new JTextArea();
        content.setText("Đây là màn hình chính của hệ thống.\n\nBạn có thể mở rộng các module ở đây.");
        content.setEditable(false);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        mainPanel.add(new JScrollPane(content), BorderLayout.CENTER);

        add(mainPanel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy thử trực tiếp MainFrame
        SwingUtilities.invokeLater(() -> {
            new MainFrame("Admin").setVisible(true);
        });
    }
}

