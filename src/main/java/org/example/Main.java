package org.example;

import javax.swing.SwingUtilities;
import org.example.view.LoginForm;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}