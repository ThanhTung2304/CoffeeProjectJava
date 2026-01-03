package org.example.controller;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;
import org.example.view.MainFrame;

import javax.swing.*;

public class AuthController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthServiceImpl();
    }

    /**
     * Xử lý đăng nhập
     * @param username
     * @param password
     * @return Account nếu thành công, null nếu sai
     */

    public Account onLogin(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Không được để trống dữ liệu");
        }

        Account acc = authService.login(
                new LoginRequest(username, password)
        );

        if (acc == null) {
            throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
        }

        return acc;
    }


    /**
     * Xử lý đăng ký
     */
    public void onRegister(String username,
                           String password,
                           String confirmPassword,
                           String role) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Confirm password không được để trống");
        }

        RegisterRequest request = new RegisterRequest(
                username.trim(),
                password,
                confirmPassword
        );

        authService.register(request);
    }

}

