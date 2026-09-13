package org.example.controller;

import org.example.entity.Account;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountController {
    private final AccountService accountService = new AccountServiceImpl();

    public List<Account> loadAll() { return accountService.findAll(); }

    public List<Account> search(String keyword, String status) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        String selectedStatus = Objects.requireNonNullElse(status, "Tất cả");
        return accountService.findAll().stream()
                .filter(a -> key.isEmpty() || (a.getUsername() != null && a.getUsername().toLowerCase().contains(key)))
                .filter(a -> "Tất cả".equals(selectedStatus) || selectedStatus.equals(a.isActive() ? "Hoạt động" : "Khóa"))
                .collect(Collectors.toList());
    }

    // Trả về null khi username chưa tồn tại để form có thể tạo tài khoản mới.
    public Account findByUsername(String username) {
        if (username == null || username.isBlank()) throw new RuntimeException("Username không hợp lệ");
        return accountService.findByUsername(username.trim());
    }

    public void add(String username, String password, String role, boolean active) {
        if (username == null || username.isBlank()) throw new RuntimeException("Username không được để trống");
        if (password == null || password.isBlank()) throw new RuntimeException("Password không được để trống");
        if (accountService.existsByUsername(username)) throw new RuntimeException("Username đã tồn tại");
        accountService.create(new Account(username.trim(), password, role, active));
    }

    public void update(int id, String username, String password, String role, boolean active) {
        if (id <= 0) throw new RuntimeException("ID không hợp lệ");
        if (username == null || username.isBlank()) throw new RuntimeException("Username không được để trống");
        Account acc = accountService.findById((long) id);
        if (acc == null) throw new RuntimeException("Tài khoản không tồn tại");
        acc.setUsername(username);
        acc.setPassword(password);
        acc.setRole(role);
        acc.setActive(active);
        accountService.update(acc);
    }

    public void delete(int id) {
        if (id <= 0) throw new RuntimeException("ID không hợp lệ");
        accountService.deleteById(id);
    }
}
