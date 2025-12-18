package org.example.controller;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;

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
    public Account login(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }

        LoginRequest request = new LoginRequest(username, password);
        return authService.login(request);
    }

    /**
     * Xử lý đăng ký
     */
    public boolean register(String username, String password, String confirmPassword) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không trùng khớp");
        }

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        return authService.register(request);
    }

//    /**
//     * Kiểm tra username đã tồn tại
//     */
//    public boolean existsByUsername(String username) {
//        return authService.existsByUsername(username);
//    }
}

