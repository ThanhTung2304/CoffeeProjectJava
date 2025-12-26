package org.example.controller;

import org.example.entity.Account;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

import java.util.List;

public class AccountController {

    private final AccountService accountService;

    public AccountController() {
        this.accountService = new AccountServiceImpl();
    }

    /* ================= LOAD ================= */

    public List<Account> loadAccounts() {
        return accountService.findAll();
    }

    /* ================= ADD ================= */

    public void addAccount(String username, String password, String role, boolean active) {

        if (username.isBlank() || password.isBlank()) {
            throw new RuntimeException("Username & Password không được trống");
        }

        if (accountService.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Account account = new Account(username, password, role, active);
        accountService.create(account);
    }

    /* ================= UPDATE ================= */

    public void updateAccount(int id, String username, String password, String role, boolean active) {

        if (id <= 0) {
            throw new RuntimeException("Account không hợp lệ");
        }

        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(role);
        account.setActive(active);

        accountService.update(account);
    }

    /* ================= DELETE ================= */

    public void deleteAccount(int id) {
        accountService.deleteById(id);
    }

}
